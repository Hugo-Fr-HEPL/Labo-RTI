GPP = g++
FLAGS = -m64 -Wall -pthread #-lnsl -lsocket
INCLUDES = -Ilib/

COMPIL = $(GPP) $(FLAGS) $(INCLUDES)

LIB = lib
BIN = bin

# Create a folder - $(@D) = the directory the current target resides in
MAKEBIN = @mkdir -p $(@D)
MAKECONF = @mkdir -p conf

EXE = Serv AppCheck
OBJSOCKET = $(BIN)/socket.o $(BIN)/socketClient.o $(BIN)/socketServer.o


ALL: $(EXE)


# Executable
Serv: $(OBJSOCKET) serveur_checkin.cpp serveur_checkin.h
	$(COMPIL) $(OBJSOCKET) -o Serv serveur_checkin.cpp
	echo Created: Serv
AppCheck: $(OBJSOCKET) application_checkin.cpp application_checkin.h
	$(COMPIL) $(OBJSOCKET) -o AppCheck application_checkin.cpp
	echo Created: AppCheck


# Objects
$(BIN)/%.o: $(LIB)/%.cpp $(LIB)/%.h
	$(MAKEBIN)
	$(MAKECONF)
	@$(COMPIL) -c $< -o $@
	echo Created: socket.o, socketClient.o, socketServer.o