#include <stdio.h>
#include "dns.h"

int main(int argc, char *argv[]) {
	char hostname[100], proxy_ip[100];
	
	int socketDescriptor;
    struct sockaddr_in sock_addr_query;

    int countBytesSendDnsQuery, countBytesReadDnsResponse;
 
    // Enter proxy IP
    printf("Enter IP proxy server: ");
    scanf("%s", proxy_ip);

 	// Create socket for dns query
    socketDescriptor = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

    if(socketDescriptor < 0) {
		perror("socketDescriptor");
		exit(ERROR_CREATE_SOCKET);
	}
 
 	memset(&sock_addr_query, 0, sizeof(sock_addr_query));

	// Set socket variables
    sock_addr_query.sin_family = AF_INET;
    sock_addr_query.sin_port = htons(5000);
    sock_addr_query.sin_addr.s_addr = inet_addr(proxy_ip);

    // Connect to proxy server
	if(connect(socketDescriptor, (struct sockaddr *)&sock_addr_query, sizeof(sock_addr_query)) < 0) {
		perror("connect");
		exit(ERROR_CONNECT_TO_PROXY);
	}

	// Send and receive data contunuously
	while(1) {
		// Enter hostname
	    printf("\nEnter Hostname to Lookup: ");
	    scanf("%s", hostname);

    	// Send DNS query
    	countBytesSendDnsQuery = SendDnsQuery(socketDescriptor, &sock_addr_query, hostname, DNS_RRT_A);

    	if(countBytesSendDnsQuery > 0) {
    		// Read DNS response
    		countBytesReadDnsResponse = ReadDnsResponse(socketDescriptor, &sock_addr_query, hostname);

    		if(countBytesReadDnsResponse < 0) {
    			printf("\nError receive DNS response!\n");
    		}
    	}
    }

    close(socketDescriptor);

    return 0;
}