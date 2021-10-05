#include "reseaux.h"


int Create_Socket(int domain, int type, int protocol) {
	int handleSocket = socket(domain, type, protocol);
	if(handleSocket == -1) {
		cout << "Erreur de creation de la socket " << errno << endl;
		exit(1);
	}
	cout << "socket n°: " << handleSocket << endl;
	return handleSocket;
}

sockaddr_in Infos_Host(properties prop) {
	struct sockaddr_in adresse;

	struct hostent *infosHost = gethostbyname(prop.machine);
	if( infosHost == 0) {
		cout << "Erreur d'acquisition d'infos sur le host " << errno << endl;
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
		cout << "Erreur sur le bind de la socket " << errno << endl;
		exit(1);
	}
}


void Listen_Server(int handleSocket, int nbConnection) {
	if(listen(handleSocket, nbConnection) == -1) {
		cout << "Erreur sur l'ecoute " << errno << endl;
		close(handleSocket);
		exit(1);
	}
}

int Accept_Server(int handleSocket, struct sockaddr *adress, int size) {
	int hSocketService = accept(handleSocket, adress, (unsigned int*)&size);
	if(hSocketService == -1) {
		cout << "Erreur sur l'acceptation " << errno << endl;
		close(handleSocket);
		exit(1);
	}
	return hSocketService;
}


void Connect_Client(int handleSocket, struct sockaddr *adress, int size) {
	if(connect(handleSocket, adress, size) == -1) {
		cout << "Erreur de connexion " << errno << endl;
		close(handleSocket);
		exit(1);
	}
}


void Send_Message(int hSocketCible, const void* message, int sizeMsg, int flagUrg) {
	if(send(hSocketCible, message, sizeMsg, flagUrg) == -1) {
		cout << "Erreur sur le send " << errno << endl;
		close(hSocketCible);
		exit(1);
	}
}

bool Receive_Message(int hSocketSource, void* message, int sizeMsg, int flagUrgdest) {
	int ret;
	if((ret = recv(hSocketSource, message, sizeMsg, flagUrgdest)) == -1) {
		cout << "Erreur sur le receive " << errno << endl;
		exit(1);
	} else if(ret == 0) {
		printf("Fin de connexion\n");
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}


properties Load_Properties(const char* nomFichier) {
	properties prop;
	FILE* fp;

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
		txt = NULL;

		fprintf(fp, ";\rPort=");
		txt = (char*)malloc(sizeof(PORT));
		sprintf(txt, "%d", PORT);
		fprintf(fp, txt, sizeof(txt));
		txt = NULL;

		fprintf(fp, ";\rServers=");
		txt = (char*)malloc(sizeof(NBSERVER));
		sprintf(txt, "%d", NBSERVER);
		fprintf(fp, txt, sizeof(txt));

		fprintf(fp, ";");
	} else {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

		char *word = NULL, *pt = NULL, *param = NULL, *val = NULL;
		for(int i = 0; ; i++) {
			word = Read_Line(i, txt);
			if(word == NULL) break;
			else {
				param = strtok_r(word, "=", &pt);
				word = NULL;
				val = strtok_r(word, ";", &pt);

				if(strcmp(param, "Host") == 0) {
					prop.machine = (char*)malloc(strlen(val));
					strcpy(prop.machine, val);
				} else if(strcmp(param, "Port") == 0)
					prop.port = atoi(val);
				else if(strcmp(param, "Servers") == 0)
					prop.nbServer = atoi(val);
			}
		}
	}
	fclose(fp);
	return prop;
}

/* Return a line from a string with multiple '\n' or '\r' */
char* Read_Line(int line, char* src) {
	char c;
	int i = 0, j = 0;

	while(i < line) {
		c = *(src+j);

		if(c == '\n' || c == '\r') {
			i++;
			j++;
		} else
			j++;

		if(c == '\0' && i < line) {
			//cout << "End Of String !!" << endl;
			return NULL;
		}
	}

	char txt[200];
	int k = 0;
	while(i == line) {
		txt[k] = *(src+j);
		if(txt[k] == '\n' || txt[k] == '\r' || txt[k] == '\0') {
			i++;
			txt[k] = '\0';
		} else {
			j++;
			k++;
		}
	}

	char* ret = (char*)malloc(sizeof(txt));
	strcpy(ret, txt);

	return ret;
}

/* Return a line directly from a file
TO DO */
char* Read_Line(int line, FILE* fp) { // TO DO for me
	return NULL;
}