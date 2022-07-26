#include <pthread.h>
#include <netdb.h>
#include <libconfig.h>

#include "lib/libstring.h"
#include "dns.h"
#include "ServerProxy.h"

//=========================================================================
// Thread function for each client
void *RunSocket(void *vargp) {
	char buffer[65535];
	int countBytesSendDnsQuery, countBytesReadDnsResponse, countBytesSendDnsResponse;

	struct SERVER_INFO *client_info = (struct SERVER_INFO *)vargp;
	struct sockaddr_in client_sd = client_info->client_sd;
	char *client_ip = inet_ntoa(client_sd.sin_addr);

	// Connect to main server via this proxy server
	int serverMainSocketDescriptor;
	struct sockaddr_in server_sd;
	socklen_t server_sd_size;

	serverMainSocketDescriptor = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

	if(serverMainSocketDescriptor < 0) {
		perror("serverMainSocketDescriptor");
		return (void *)1;
	}

	memset(&server_sd, 0, sizeof(server_sd));

	server_sd.sin_family = AF_INET;
	server_sd.sin_port = htons(client_info->server_main_port);
	server_sd.sin_addr.s_addr = inet_addr(client_info->server_main_ip);

	if(connect(serverMainSocketDescriptor, (struct sockaddr *)&server_sd, sizeof(server_sd)) < 0) {
		perror("serverMainSocketDescriptor");
		return (void *)2;
	}

	// Send DNS request
	countBytesSendDnsQuery = sendto(
	    serverMainSocketDescriptor,
	    (char *)client_info->dns_request,
	    client_info->dns_request_len, 
	    0, 
	    (struct sockaddr *)&server_sd,
	    sizeof(server_sd)
	);

	if(countBytesSendDnsQuery > 0) {
		countBytesReadDnsResponse = recvfrom(serverMainSocketDescriptor, buffer, sizeof(buffer), 0, (struct sockaddr *)&server_sd, &server_sd_size);

		if(countBytesReadDnsResponse > 0) {
			printf(
				"Received DNS response from server with IP '%s' to client with IP '%s' for resolving domain name: %s\n", 
				client_info->server_main_ip, 
				client_ip, 
				client_info->hostname
			);

		    countBytesSendDnsResponse = sendto(
				client_info->client_socket,
				buffer,
				countBytesReadDnsResponse, 
				0, 
				(struct sockaddr *)&client_sd,
				sizeof(client_sd)
			);

			if(countBytesSendDnsResponse > 0) {
				printf(
					"Send DNS response to client with IP '%s' for resolving domain name: %s\n", 
					client_ip, 
					client_info->hostname
				);
			}
			else {
				printf(
					"Error send DNS response to client with IP '%s' for resolving domain name: %s\n", 
					client_ip, 
					client_info->hostname
				);
			}
		}
		else {
			printf(
				"Error receive DNS response from server with IP '%s' to client with IP '%s' for resolving domain name: %s\n", 
				client_info->server_main_ip, 
				client_ip, 
				client_info->hostname
			);
		}
	}
	else {
		printf(
			"Error send DNS query from client with IP '%s' to server with IP '%s' for resolving domain name: %s\n", 
			client_ip, 
			client_info->server_main_ip, 
			client_info->hostname
		);
	}

	close(serverMainSocketDescriptor);

	return NULL;
}

//=========================================================================
// Main function
int main(void) {
	struct SERVER_CONFIG serverProxyConfig;
	int listenSocketDescriptor;
	struct sockaddr_in proxy_sd, client_sd;
	socklen_t client_sd_size;

	int countBytesReadDnsQuery;
	char buffer[65536];

	// Read config file
	if(ReadConfig(&serverProxyConfig) == FALSE) {
		printf("Can't read configuration file!\n");
		exit(ERROR_CAN_NOT_READ_CONFIG);
	}

	signal(SIGPIPE, SIG_IGN);

	// Create listen socket
	listenSocketDescriptor = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

	if(listenSocketDescriptor < 0) {
		perror("Error create listen socket");
		exit(ERROR_CREATE_LISTEN_SOCK);
	}

	memset(&proxy_sd, 0, sizeof(proxy_sd));

	// Set variables for listen socket
	proxy_sd.sin_family = AF_INET;
	proxy_sd.sin_port = htons(serverProxyConfig.serverProxyPort);
	proxy_sd.sin_addr.s_addr = htonl(INADDR_ANY);

	if(bind(listenSocketDescriptor, (struct sockaddr *)&proxy_sd, sizeof(proxy_sd)) < 0) {
		perror("Error bind listen socket");
		exit(ERROR_BIND_LISTEN_SOCK);
	}

	// Start listen for a new connections
	listen(listenSocketDescriptor, SOMAXCONN);

	printf("Server Proxy run!\n");

	// Loop for proccess connections
	while(1) {
		printf("\nWaiting for connection...\n");
		fflush(stdout);

		// Read DNS query from clients
		countBytesReadDnsQuery = recvfrom(listenSocketDescriptor, buffer, sizeof(buffer), 0, (struct sockaddr *)&client_sd, &client_sd_size);

		if(countBytesReadDnsQuery > 0) {
			// Get current client IP
			char *client_ip = inet_ntoa(client_sd.sin_addr);

			// Get hostname for resolve
			char *hostname = GetHostNameFromDnsQuery(buffer);

			printf("\nReceived DNS query from client with IP '%s' for resolving domain name: %s\n", client_ip, hostname);

			// Checking resolved domain name in blacklist
			printf("Checking domain name '%s' in blacklist...\n", hostname);
			
			int is_host_in_blackList = CheckHostNameInBlackList(hostname, &serverProxyConfig);

			printf("Domain name '%s'", hostname);

			if(is_host_in_blackList == TRUE) {
				printf(" is in blacklist!\nSend response to client with IP '%s'!\n", client_ip);
			}
			else {
				printf(" is not in blacklist!\nSend DNS query to DNS server with IP '%s'...\n", serverProxyConfig.serverMainIP);
			}

			// Create struct with variables for current connection
			struct SERVER_INFO *server_info = (struct SERVER_INFO *)malloc(sizeof(struct SERVER_INFO));

			server_info->client_socket = listenSocketDescriptor;
			server_info->client_sd = client_sd;

			server_info->server_main_ip = (char *)malloc(strlen(serverProxyConfig.serverMainIP) + 1);
			memset(server_info->server_main_ip, '\0', strlen(server_info->server_main_ip));
			strcpy(server_info->server_main_ip, serverProxyConfig.serverMainIP);

			server_info->server_main_port = serverProxyConfig.serverMainPort;

			memcpy(server_info->dns_request, buffer, countBytesReadDnsQuery);
			server_info->dns_request_len = countBytesReadDnsQuery;

			server_info->hostname = (char *)malloc(strlen(hostname) + 1);
			memset(server_info->hostname, '\0', strlen(server_info->hostname));
			strcpy(server_info->hostname, hostname);

			// Create new thread for a new client
			pthread_t tid;
			pthread_create(&tid, NULL, RunSocket, (void *)server_info);

			sleep(1);

		}
	}

	close(listenSocketDescriptor);

	return 0;
}

//=========================================================================
// Read config file
int ReadConfig(struct SERVER_CONFIG *paramServerProxyConfig) {
	config_t config;
	config_setting_t *config_setting, *config_setting_element;

	int i, count;

	const char *serverMainIPLocal;
	const char *blacklistDomain, *blacklistDescription;

	// Init config file
	config_init(&config);

	// Read config file
	if(!config_read_file(&config, "config.cfg")) {
		fprintf(stderr, "%s:%d - %s\n", config_error_file(&config), config_error_line(&config), config_error_text(&config));
		config_destroy(&config);
		return FALSE;
	}

	// Read  server main IP
	if(!config_lookup_string(&config, SERVER_MAIN_IP, &serverMainIPLocal)) {
		fprintf(stderr, "No '"SERVER_MAIN_IP"' setting in configuration file!\n");
		config_destroy(&config);
		return FALSE;
	}

	paramServerProxyConfig->serverMainIP = malloc(strlen(serverMainIPLocal) + 1);
	strcpy(paramServerProxyConfig->serverMainIP, serverMainIPLocal);

	// Read server main port
	if(!config_lookup_int(&config, SERVER_MAIN_PORT, &(paramServerProxyConfig->serverMainPort))) {
		fprintf(stderr, "No '"SERVER_MAIN_PORT"' setting in configuration file!\n");
		config_destroy(&config);
		return FALSE;
	}

	// Read server proxy port
	if(!config_lookup_int(&config, SERVER_PROXY_PORT, &(paramServerProxyConfig->serverProxyPort))) {
		fprintf(stderr, "No '"SERVER_PROXY_PORT"' setting in configuration file!\n");
		config_destroy(&config);
		return FALSE;
	}

	// Read blacklist of domains
	config_setting = config_lookup(&config, SERVER_BLACKLIST);

	if(config_setting != NULL) {
		count = config_setting_length(config_setting);

		// Save blacklist count
		paramServerProxyConfig->blacklist_len = count;

		if(count > 0) {
			paramServerProxyConfig->blacklist = (struct BLACKLIST **)malloc(count * sizeof(struct BLACKLIST *));

			for(i = 0; i < count; i++) {
				config_setting_element = config_setting_get_elem(config_setting, i);

				paramServerProxyConfig->blacklist[i] = (struct BLACKLIST *)malloc(sizeof(struct BLACKLIST));

				// Read info about each domain from blacklist
				if(config_setting_lookup_string(config_setting_element, BLACKLIST_DOMAIN, &blacklistDomain)) {
					paramServerProxyConfig->blacklist[i]->domain = (char *)malloc(strlen(blacklistDomain) + 1);
					memset(paramServerProxyConfig->blacklist[i]->domain, '\0', strlen(paramServerProxyConfig->blacklist[i]->domain));

					strcpy(paramServerProxyConfig->blacklist[i]->domain, blacklistDomain);
				}

				if(config_setting_lookup_string(config_setting_element, BLACKLIST_DESCRIPTION, &blacklistDescription)) {
					paramServerProxyConfig->blacklist[i]->description = (char *)malloc(strlen(blacklistDescription) + 1);
					memset(paramServerProxyConfig->blacklist[i]->description, '\0', strlen(paramServerProxyConfig->blacklist[i]->description));

					strcpy(paramServerProxyConfig->blacklist[i]->description, blacklistDescription);
				}
			}
		}
	}

	// Destroy config
	config_destroy(&config);

	return TRUE;
}

//=========================================================================
int HostnameToIP(char *paramHostname, char *paramIP) {
	struct hostent *host;			// pointer to struct contain info about host
	struct in_addr **addr_list;		// list of addresses for host
	int i;

	// Get info about host
	host = gethostbyname(paramHostname);

	if(host == NULL) {
		herror("gethostbyname");
		return FALSE;
	}

	// Get list addresses for host
	addr_list = (struct in_addr **)host->h_addr_list;

	for(i = 0; addr_list[i] != NULL; i++) {
		// Return the first IP
		strcpy(paramIP, inet_ntoa(*addr_list[i]));
		return TRUE;
	}

	return FALSE;
}

//=========================================================================
// Function for check is exist hostname in blacklist
int CheckHostNameInBlackList(char *param_hostname, struct SERVER_CONFIG *paramServerProxyConfig) {
	int is_host_in_blackList = FALSE;
	int i, j;
	int count_hostname_by_dote, count_cur_black_domain_by_dote;

	char **array_hostname_by_dote;
	char **array_cur_black_domain_by_dote;

	if(param_hostname != NULL && paramServerProxyConfig != NULL) {
		array_hostname_by_dote = stringSplit(param_hostname, ".", &count_hostname_by_dote);

		for(i = 0; i < paramServerProxyConfig->blacklist_len; i++) {
			struct BLACKLIST *cur_black_domain = paramServerProxyConfig->blacklist[i];

			if(cur_black_domain != NULL) {
				array_cur_black_domain_by_dote = stringSplit(cur_black_domain->domain, ".", &count_cur_black_domain_by_dote);

				if(array_cur_black_domain_by_dote != NULL && array_hostname_by_dote != NULL && count_hostname_by_dote >= count_cur_black_domain_by_dote) {
					for(j = count_cur_black_domain_by_dote - 1; j >= 0; j--) {
						if(stringCompare(array_hostname_by_dote[j], array_cur_black_domain_by_dote[j]) != 0 && 
						   stringCompare(array_cur_black_domain_by_dote[j], "*") != 0)
						{
							break;
						}

						if(j == 0) {
							is_host_in_blackList = TRUE;
						}
					}
				}
			}

			if(is_host_in_blackList == TRUE) {
				break;
			}
		}
	}

	return is_host_in_blackList;
}