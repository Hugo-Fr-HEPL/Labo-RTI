#include "Application_CIAChat.h"


int main() {
	char msgServeur[MAXSTRING];
	char msg[50] = "";
	char addr_udp[15], port_udp[5], nom[20];


	ConnectionTCP();

	Login();


// Récupération de l'adresse et du port UDP
	sock.Receive_Message(cli.hSock, msgServeur, 0);

	strcpy(addr_udp, sock.Sub_Msg(msg, msgServeur, '#', '$', 0));
	strcpy(port_udp, sock.Sub_Msg(msg, msgServeur, '#', '$', 1));
	strcpy(nom, sock.Sub_Msg(msg, msgServeur, '#', '$', 2));


	struct sockaddr_in adresse = ConnectionUDP(addr_udp, port_udp);


	strcpy(msg, nom);
	strcat(msg, " <5> Salut Ma biche !");
	sendto(cli.hSock, msg, sizeof(msg), 0, (struct sockaddr*)NULL, sizeof(struct sockaddr_in));
	cout << "oui "<< endl;

	unsigned int tailleSockaddr_in = sizeof(struct sockaddr_in);
	recvfrom(cli.hSock, msgServeur, MAXSTRING, 0, (struct sockaddr*)&adresse, &tailleSockaddr_in);
	puts(msgServeur);

	cout << "REcu " << msgServeur << endl;

	//MainLoop();

                //msg = nomCli +" <"+ (Math.round(Math.random() * 98) + 1) +"> "+ jTextMsg.getText();

	CloseConnection();
}


/*
	Se connecte au serveur TCP
*/
void ConnectionTCP() {
	properties prop = sock.Load_Properties(PROPFILE);
	cli.hSock = sock.Create_Socket(AF_INET, SOCK_STREAM, 0);
	struct sockaddr_in adresse = sock.Infos_Host(prop);
	sock.Connect_Client(cli.hSock, (struct sockaddr*)&adresse);
}


/*
	Se connecte au canal UDP
*/
sockaddr_in ConnectionUDP(char* add, char* port) {
	cli.hSock = sock.Create_Socket(AF_INET, SOCK_DGRAM, 0);
	struct sockaddr_in adresse = sock.Infos_Host(add, port);
	sock.Connect_Client(cli.hSock, (struct sockaddr*)&adresse);

	return adresse;
}


/*
	Envoie ses infos de connexion au serveur
	return EXIT_SUCCESS on success
*/
void Login() {
	char msgClient[MAXSTRING] = "";
	char login[30] = "Blair", password[30] = "mdpBlair";

/*
	char login[30], password[30];
	cout << "Encodez votre login : ";
	cin >> login;
	cout << "Encodez votre mot de passe : ";
	cin >> password;
*/

// Message for the server to know it's a C application
	strcat(msgClient, LOGIN_GROUP);
	strcat(msgClient, "#");
	strcat(msgClient, LOGIN_C);
	strcat(msgClient, "#");
	strcat(msgClient, login);
	strcat(msgClient, "#");
	strcat(msgClient, password);
	strcat(msgClient, "$");


	sock.Send_Message(cli.hSock, msgClient, 0);
}


/*
	Ferme tout proprement
*/
void CloseConnection() {
	close(cli.hSock);
    printf("Socket client ferme\n");
	exit(EXIT_FAILURE);
}