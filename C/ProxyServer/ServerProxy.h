#ifndef ServerProxy
#define ServerProxy

#define TRUE 1
#define FALSE 0

#define SERVER_MAIN_IP 			"server_main_ip"
#define SERVER_MAIN_PORT 		"server_main_port"
#define SERVER_PROXY_PORT 		"server_proxy_port"
#define SERVER_BLACKLIST 		"blacklist"
#define BLACKLIST_DOMAIN		"domain"
#define BLACKLIST_DESCRIPTION	"description"

#define ERROR_CAN_NOT_READ_CONFIG 	1
#define ERROR_CREATE_LISTEN_SOCK 	2
#define ERROR_BIND_LISTEN_SOCK		3

// Structure to maintain client fd, and server ip address and port address
// client will establish connection to server using given IP and port
struct SERVER_INFO {
	int client_socket;
	struct sockaddr_in client_sd;
	char *server_main_ip;
	int server_main_port;
	char *hostname;
	char dns_request[65536];
	unsigned int dns_request_len;
};

// Structure to store blacklist domains
struct BLACKLIST {
	char *domain;
	char *description;
};

// Structure to store server config
struct SERVER_CONFIG {
	char *serverMainIP;
	int serverMainPort;
	int serverProxyPort;
	struct BLACKLIST **blacklist;
	unsigned int blacklist_len;
};

int HostnameToIP(char *paramHostname, char *paramIP) ;
int ReadConfig(struct SERVER_CONFIG *paramServerProxyConfig);
int CheckHostNameInBlackList(char *param_hostname, struct SERVER_CONFIG *paramServerProxyConfig);
#endif