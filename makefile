GPP = g++
FLAGS = -m64 -Wall -pthread -Ilib/ #-lnsl -lsocket
INCLUDES = -Ilib/ -Ibin/

LIB = lib
BIN = bin

# Create a folder - $(@D) = the directory the current target resides in
MAKEBIN = @mkdir -p $(@D)
MAKECONF = @mkdir -p conf

OBJSOCKET = $(BIN)/socket.o $(BIN)/socketClient.o $(BIN)/socketServer.o


ALL: Serv AppCheck


# Executable
Serv: $(OBJSOCKET) serveur_checkin.cpp serveur_checkin.h
	$(GPP) $(FLAGS) $(OBJSOCKET) -o Serv serveur_checkin.cpp
	echo Created: Serv
AppCheck: $(OBJSOCKET) application_checkin.cpp
	$(GPP) $(FLAGS) $(OBJSOCKET) -o AppCheck application_checkin.cpp
	echo Created: AppCheck


# Objects
$(BIN)/%.o: $(LIB)/%.cpp $(LIB)/%.h $(FOLDER)
	$(MAKEBIN)
	$(MAKECONF)
	@$(GPP) $(FLAGS) -c $< -o $@
	echo Created: socket.o, socketClient.o, socketServer.o