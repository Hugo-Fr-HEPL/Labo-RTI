#include "socketClient.h"


void SocketClient::Connect_Client(int handleSocket, struct sockaddr *adress) {
	if(connect(handleSocket, adress, sizeof(struct sockaddr_in)) == -1) {
		cout << "Erreur de connexion " << errno << endl;
		close(handleSocket);
		exit(1);
	}
}