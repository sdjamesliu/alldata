default: test

include ../../../helpers/examples.mk

RELEASE := helm-kibana-default

install:
	helm upgrade --wait --timeout=$(TIMEOUT) --install $(RELEASE) ../../

test: install goss

purge:
	helm del $(RELEASE)
