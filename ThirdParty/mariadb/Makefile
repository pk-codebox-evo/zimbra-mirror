MARIADB_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(MARIADB_ROOT)/../..; pwd)

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)

MAKE ?= make
MAKEARGS ?= -j2

ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif

ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(MARIADB_ROOT)/../beta_versions.def
else
	include $(MARIADB_ROOT)/../versions.def
endif

MARIADB_VERSION ?= $(MARIADB_VERSION_STRING)

LDFLAGS	:= LDFLAGS="-L$(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/lib -R$(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/lib -R$(ZIMBRA_HOME)/lib -R$(OPENSSL_LIB_DIR)"
ifeq ($(BUILD_PLATFORM), UBUNTU12_64)
	LDFLAGS	:= LDFLAGS="-L$(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/lib"
endif
ifeq ($(BUILD_PLATFORM), RHEL7_64)
	LDFLAGS	:= LDFLAGS="-L$(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/lib"
endif

MARIADB_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/mariadb/mariadb-$(MARIADB_VERSION).tar.gz

MARIADB_LIB_TGZ_TARGET := \
	$(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/mariadb/mariadb-libs-$(MARIADB_VERSION).tgz

files   := $(wildcard src/mariadb-$(MARIADB_VERSION_STRING).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

clean:
	rm -rf mariadb-$(MARIADB_VERSION_STRING)

build:
	mkdir -p $(ZIMBRA_HOME)
	rm -rf mariadb-$(MARIADB_VERSION_STRING)
	tar xzf src/mariadb-$(MARIADB_VERSION_STRING).tar.gz
	(cd mariadb-$(MARIADB_VERSION_STRING); \
	$(PATCH) \
	CFLAGS="-O3 -fno-omit-frame-pointer -g -pipe -Wall -Wno-uninitialized" \
	CXXFLAGS="-O3 -fno-omit-frame-pointer -g -pipe -Wall -Wno-uninitialized" \
	$(LDFLAGS) \
	CPPFLAGS="-I/opt/zimbra/openssl-$(OPENSSL_VERSION)/include -I/opt/zimbra/tcmalloc-$(TCMALLOC_VERSION)/include" \
	/usr/bin/cmake . -DCMAKE_SKIP_BUILD_RPATH=FALSE \
		-DCMAKE_BUILD_WITH_INSTALL_RPATH=FALSE  \
		-DCMAKE_INSTALL_RPATH="/opt/zimbra/openssl-$(OPENSSL_VERSION)/lib" \
		-DCMAKE_PREFIX_PATH=/opt/zimbra/openssl-$(OPENSSL_VERSION) -DWITH_SSL=yes \
		-DCMAKE_INSTALL_PREFIX=/opt/zimbra/mariadb-$(MARIADB_VERSION) \
		-DBUILD_CONFIG=mysql_release \
		-DCOMPILATION_COMMENT="Zimbra binary distribution" \
		-DWITH_SAFEMALLOC=OFF \
		-DWITH_FAST_MUTEXES=ON; \
	LD_RUN_PATH=$(OPENSSL_LIB_DIR) $(MAKE) $(MAKEARGS); \
	$(MAKE) install)
	(cd $(ZIMBRA_HOME); rm -f mariadb; \
	rm -rf $(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/mysql-test; \
	rm -rf $(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/bin/*test*; \
	rm -rf $(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/bin/*embed*; \
	rm -rf $(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)/lib/libmysqld*; \
	ln -s mariadb-$(MARIADB_VERSION) mariadb)

tar:
	-mkdir -p $(shell dirname $(MARIADB_TGZ_TARGET))
	(cd $(ZIMBRA_HOME); tar czvf $(MARIADB_LIB_TGZ_TARGET) mariadb-$(MARIADB_VERSION)/lib)
	(cd $(ZIMBRA_HOME); tar czvf $(MARIADB_TGZ_TARGET) mariadb-$(MARIADB_VERSION)/bin mariadb-$(MARIADB_VERSION)/include mariadb-$(MARIADB_VERSION)/scripts mariadb-$(MARIADB_VERSION)/share mariadb-$(MARIADB_VERSION)/sql-bench mariadb-$(MARIADB_VERSION)/support-files)

p4edit: $(MARIADB_TGZ_TARGET) 
	p4 add $(MARIADB_TGZ_TARGET) $(MARIADB_LIB_TGZ_TARGET)
	p4 edit $(MARIADB_TGZ_TARGET) $(MARIADB_LIB_TGZ_TARGET)

allclean: clean
	rm -rf $(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)
	rm -rf $(ZIMBRA_HOME)/mariadb
	rm -rf $(ZIMBRA_HOME)/mariadb-$(MARIADB_VERSION)
	rm -f $(MARIADB_TGZ_TARGET)
	rm -f $(MARIADB_LIB_TGZ_TARGET)
