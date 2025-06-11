PROJECT_PATH=$(shell pwd)

TARGET_DIR=bin

ECHO_SERVER=echo-server/Server
ECHO_CLIENT=echo-server/Client

MODULE_DIR=build/libs
MODULE_JAR=*-all.jar

all: build move_jar

config:
	@if [ ! -d $(TARGET_DIR) ]; then mkdir $(TARGET_DIR); fi

build:
	./gradlew clean build

move_jar:
	cp $(ECHO_SERVER)/$(MODULE_DIR)/$(MODULE_JAR) $(PROJECT_PATH)/$(TARGET_DIR)
	cp $(ECHO_CLIENT)/$(MODULE_DIR)/$(MODULE_JAR) $(PROJECT_PATH)/$(TARGET_DIR)

target-version:
	@echo "========================================"
	@echo "APP_VERSION    : $(VERSION_NUM)"
	@echo "BUILD_NUM      : $(BUILD_NUM)"
	@echo "TARGET_VERSION : $(TARGET_VERSION)"
	@echo "========================================"