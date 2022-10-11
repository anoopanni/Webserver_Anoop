#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <netinet/in.h>
#include <sys/socket.h>

int main(){

int server_fd = socket(AF_INET, SOCK_STREAM, 0);
if(server_fd < 0){
    perror("cannot create socket");
    exit(1);
}
// printf("%d\n", server_fd);

struct sockaddr_in address;
const int PORT = 8080;
memset((char *)&address, 0, sizeof(address)); 

address.sin_family = AF_INET; 
/* The htonl() function translates a long integer from host byte order to network byte order. 
htonl is host-to-network long, This means it works on 32-bit long integers. i.e. 4 bytes */ 
address.sin_addr.s_addr = htonl(INADDR_ANY); 
/* htons converts a short integer (e.g. port) to a network representation
htons is host-to-network short. This means it works on 16-bit short integers. i.e. 2 bytes */
address.sin_port = htons(PORT); 


// Binding the socket with an address and port number
if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) 
{ 
    perror("bind failed"); 
    exit(1);
}

    return 0;
}


// Wait for an incoming connection 

