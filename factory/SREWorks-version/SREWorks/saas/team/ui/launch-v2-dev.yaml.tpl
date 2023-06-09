apiVersion: core.oam.dev/v1alpha2
kind: ApplicationConfiguration
metadata:
  name: deploy-team-package
  annotations:
    appId: team
    clusterId: master
    namespaceId: ${NAMESPACE_ID}
    stageId: dev
spec:
  components:
  - dataInputs: []
    dataOutputs: []
    dependencies: []
    revisionName: INTERNAL_ADDON|productopsv2|_
    scopes:
    - scopeRef:
        apiVersion: flyadmin.alibaba.com/v1alpha1
        kind: Namespace
        name: ${NAMESPACE_ID}
    - scopeRef:
        apiVersion: flyadmin.alibaba.com/v1alpha1
        kind: Stage
        name: 'dev'
    - scopeRef:
        apiVersion: flyadmin.alibaba.com/v1alpha1
        kind: Cluster
        name: 'master'
    parameterValues:
    - name: TARGET_ENDPOINT
      value: "${CORE_STAGE_ID}-${CORE_APP_ID}-paas-action"
      toFieldPaths:
        - spec.targetEndpoint
    - name: STAGE_ID
      value: 'dev'
      toFieldPaths:
        - spec.stageId
    traits:
    - name: gateway.trait.abm.io
      runtime: post
      spec:
        path: /sreworks/teammanage/**
        servicePort: 80
        serviceName: "prod-team-team"
        routeId: "dev-team-team-master-${NAMESPACE_ID}-dev"

