LIB_DIR		=	./libbcejni
LIB_COPY_DIR	=	./bcejni/src/main/resources

.PHONY: all
all: lib-build mvn-build
	@echo "Done building all submodules"

.PHONY: lib-build
lib-build:
	@echo 'Building the native library'
	cd $(LIB_DIR); make
	cp $(LIB_DIR)/target/libbcejni.* $(LIB_COPY_DIR)

.PHONY: mvn-build
mvn-build:
	@echo 'Build the Maven projects'
	mvn clean install

.PHONY: clean
clean:
	cd $(LIB_DIR); make clean
	rm -f $(LIB_COPY_DIR)/libbcejni.*
	mvn clean
