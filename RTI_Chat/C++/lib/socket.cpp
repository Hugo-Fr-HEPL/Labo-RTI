#include "socket.h"


int Socket::Create_Socket(int domain, int type, int protocol) {
	int handleSocket = socket(domain, type, protocol);
	if(handleSocket == -1) {
		cout << "Erreur de creation de la socket " << errno << endl;
		exit(1);
	}
	cout << "socket n°: " << handleSocket << endl;
	return handleSocket;
}

sockaddr_in Socket::Infos_Host(properties prop) {
	struct sockaddr_in adresse;

	//adresse.sin_addr.s_addr = inet_addr("192.168.4.138");
	//struct hostent *infosHost = getaddrinfo();

	struct hostent *infosHost = gethostbyname(prop.address_tcp);
	if(infosHost == 0) {
		cout << "Erreur d'acquisition d'infos sur le host " << errno << endl;
		exit(1);
	}

	memset(&adresse, 0, sizeof(struct sockaddr_in));
	adresse.sin_family = AF_INET;
	adresse.sin_port = htons(prop.port_tcp);
	memcpy(&adresse.sin_addr, infosHost->h_addr, infosHost->h_length);

	return adresse;
}
sockaddr_in Socket::Infos_Host(char* add, char* port) {
	struct sockaddr_in adresse;

	memset(&adresse, 0, sizeof(struct sockaddr_in));
	adresse.sin_addr.s_addr = inet_addr(add);
	adresse.sin_port = htons(atoi(port));
	adresse.sin_family = AF_INET;

	return adresse;
}


void Socket::Send_Message(int hSocketCible, const void* message, int flagUrg) {
	if(send(hSocketCible, message, MAXSTRING, flagUrg) == -1) {
		cout << "Erreur sur le send " << errno << endl;
		close(hSocketCible);
		exit(1);
	}
}

bool Socket::Receive_Message(int hSocketSource, void* message, int flagUrgdest) {
	int ret;
	if((ret = recv(hSocketSource, message, MAXSTRING, flagUrgdest)) == -1) {
		cout << "Erreur sur le receive " << errno << endl;
		exit(1);
	} else if(ret == 0) {
        cout << "Fin de connexion" << endl;
		return EXIT_FAILURE;
	}
	return EXIT_SUCCESS;
}

char* Socket::Sub_Msg(char* ret, char* msg, char sep, char end, int num) {
	int k = 0;
	for(int i = 0, j = 0; *(msg+i) != end; i++) {
		if(*(msg+i) == sep) {
			j++;
		} else {
			if(j == num) {
				*(ret+k) = *(msg+i);
				k++;
			} else if(j > num)
				break;
		}
	}
	*(ret+k) = '\0';
	return ret;
}


properties Socket::Load_Properties(const char* nomFichier) {
	properties prop;
	FILE* fp;

	fp = fopen(nomFichier, "r+t");
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

			if(strcmp(param, "Host_tcp") == 0) {
				prop.address_tcp = (char*)malloc(strlen(val));
				strcpy(prop.address_tcp, val);
			} else if(strcmp(param, "Port_tcp") == 0) {
				prop.port_tcp = atoi(val);
			} else if(strcmp(param, "Servers") == 0) {
				prop.nbServer = atoi(val);
			} else if(strcmp(param, "Host_udp") == 0) {
				prop.address_udp = (char*)malloc(strlen(val));
				strcpy(prop.address_udp, val);
			} else if(strcmp(param, "Port_udp") == 0) {
				prop.port_udp = atoi(val);
			}
		}
	}
	fclose(fp);
	return prop;
}

/* Return a line from a string with multiple '\n' or '\r' */
char* Socket::Read_Line(int line, char* src) {
	char c;
	int i = 0, j = 0;

	while(i < line) {
		c = *(src+j);

		if(c == '\n' || c == '\r') {
			i++;
			j++;
			if(*(src+j) == '\n' || *(src+j) == '\r') {
				j++;
			}
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