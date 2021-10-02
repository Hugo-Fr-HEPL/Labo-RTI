#include "reseaux.h"


int Create_Socket(int domain, int type, int protocol) {
	int handleSocket = socket(domain, type, protocol);
	if(handleSocket == -1) {
		printf("Erreur de creation de la socket %d\n", errno);
		exit(1);
	}
	printf("socket n°: %d\n", handleSocket);
	return handleSocket;
}

sockaddr_in Infos_Host(properties prop) {
	struct sockaddr_in adresse;

	struct hostent *infosHost = gethostbyname(prop.machine);
	if( infosHost == 0) {
		printf("Erreur d'acquisition d'infos sur le host %d\n", errno);
		exit(1);
	}

	memset(&adresse, 0, sizeof(struct sockaddr_in));
	adresse.sin_family = AF_INET;
	adresse.sin_port = htons(prop.port);
	memcpy(&adresse.sin_addr, infosHost->h_addr, infosHost->h_length);

	return adresse;
}

void Bind_Socket(int handleSocket, const struct sockaddr *adress, int size) {
	if(bind(handleSocket, adress, size) == -1) {
		printf("Erreur sur le bind de la socket %d\n", errno);
		exit(1);
	}
}


void Listen_Server(int handleSocket, int nbConnection) {
	if(listen(handleSocket, nbConnection) == -1) {
		printf("Erreur sur l'ecoute %d\n", errno);
		close(handleSocket);
		exit(1);
	}
}

int Accept_Server(int handleSocket, struct sockaddr *adress, int size) {
	int hSocketService = accept(handleSocket, adress, (unsigned int*)&size);
	if(hSocketService == -1) {
		printf("Erreur sur l'acceptation %d\n", errno);
		close(handleSocket);
		exit(1);
	}
	return hSocketService;
}


void Connect_Client(int handleSocket, struct sockaddr *adress, int size) {
	if(connect(handleSocket, adress, size) == -1) {
		printf("Erreur de connexion %d\n", errno);
		close(handleSocket);
		exit(1);
	}
}


void Send_Message(int hSocketCible, const void* message, int sizeMsg, int flagUrg) {
//	if(send(hSocketCible, message, sizeMsg, flagUrg) == -1) {
//		printf("Erreur sur le send %d\n", errno);
//		close(hSocketCible);
//		exit(1);
//	} else
//		printf("Send socket refusee OK\n");
//	close(hSocketCible);
	int reussite;
	reussite = send(hSocketCible, message, sizeMsg, flagUrg);
	if(reussite==-1)
	{
		printf("Erreur sur le send %d \n",errno);
		exit(1);
	}
}

char* Receive_Message(int hSocketSource, void* message, int sizeMsg, int flagUrgdest) {
	if(recv(hSocketSource, message, sizeMsg, flagUrgdest) == -1) {
		printf("Erreur sur le receive %d\n", errno);
		exit(1);
	}
	printf("Message: %s\n",message);
	return (char*)message;
}


properties Load_Properties(const char* nomFichier) {
	properties prop;
	FILE *fp;

	fp = fopen(nomFichier, "r+t");
	if(fp == NULL) {
		fp = fopen(nomFichier, "w+t");

		char hostname[200];
		gethostname(hostname, 200);

		prop.machine = (char*)malloc(strlen(hostname));
		strcpy(prop.machine, hostname);
		prop.port = PORT;
		prop.nbServer = NBSERVER;

		char* txt = (char*)malloc(10);

		fprintf(fp, "Host=");
		fprintf(fp, prop.machine, sizeof(prop.machine));
		fprintf(fp, ";\rPort=");
		sprintf(txt, "%d", PORT);
		fprintf(fp, txt);
		fprintf(fp, ";\rServers=");
		sprintf(txt, "%d", NBSERVER);
		fprintf(fp, txt);
		fprintf(fp, ";");
	} else {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

		char* word = Read_Lines(1, txt);
		prop.machine = (char*)malloc(strlen(word));
		strcpy(prop.machine, word);

		prop.port = atoi(Read_Lines(2, txt));

		prop.nbServer = atoi(Read_Lines(3, txt));
	}
	fclose(fp);
	return prop;
}

char* Read_Lines(int line, char* txt) {
	char prop[200], l;
	int i = 0;

	while(i < line) {
		l = *txt;
		if(l == '=') {
			i++;
			txt++;
		} else
			txt++;
	}

	i = 0;
	while(1) {
		l = *txt;
		if(l == ';')
			break;
		else {
			prop[i] = *txt;
			i++;
			txt++;
		}
	}
	prop[i] = '\0';

	char* ret = (char*)malloc(sizeof(prop));
	strcpy(ret, prop);

	return ret;
}
