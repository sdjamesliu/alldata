default: test

include ../../../helpers/examples.mk

RELEASE := helm-kibana-oss

install:
	helm upgrade --wait --timeout=$(TIMEOUT) --install --values values.yaml $(RELEASE) ../../

test: install goss

purge:
	helm del $(RELEASE)
