"""
Generated by Erda Migrator.
Please implement the function entry, and add it to the list entries.
"""

import django.db.models

class ErdaDeploymentOrder(django.db.models.Model):
    """
    generated by erda-cli
    """

    id = django.db.models.CharField()
    type = django.db.models.CharField()
    description = django.db.models.TextField()
    release_id = django.db.models.CharField()
    operator = django.db.models.CharField()
    project_id = django.db.models.BigIntegerField()
    project_name = django.db.models.CharField()
    application_id = django.db.models.BigIntegerField()
    application_name = django.db.models.CharField()
    workspace = django.db.models.CharField()
    status = django.db.models.TextField()
    params = django.db.models.TextField()
    is_outdated = django.db.models.BooleanField()
    created_at = django.db.models.DateTimeField(auto_now=True)
    updated_at = django.db.models.DateTimeField(auto_now=True, auto_now_add=True)
    started_at = django.db.models.DateTimeField()
    batch_size = django.db.models.BigIntegerField()
    current_batch = django.db.models.BigIntegerField()
    deploy_list = django.db.models.TextField()
    
    class Meta:
        db_table = "erda_deployment_order"

class DiceRelease(django.db.models.Model):
    """
    generated by erda-cli
    """

    release_id = django.db.models.CharField(primary_key=True)
    release_name = django.db.models.CharField()
    desc = django.db.models.TextField()
    dice = django.db.models.TextField()
    addon = django.db.models.TextField()
    labels = django.db.models.CharField()
    version = django.db.models.CharField()
    org_id = django.db.models.BigIntegerField()
    project_id = django.db.models.BigIntegerField()
    application_id = django.db.models.BigIntegerField()
    project_name = django.db.models.CharField()
    application_name = django.db.models.CharField()
    user_id = django.db.models.CharField()
    cluster_name = django.db.models.CharField()
    cross_cluster = django.db.models.BooleanField()
    resources = django.db.models.TextField()
    reference = django.db.models.BigIntegerField()
    created_at = django.db.models.DateTimeField()
    updated_at = django.db.models.DateTimeField()
    changelog = django.db.models.TextField()
    is_stable = django.db.models.BooleanField()
    is_formal = django.db.models.BooleanField()
    is_project_release = django.db.models.BooleanField()
    application_release_list = django.db.models.TextField()
    tags = django.db.models.CharField()
    git_branch = django.db.models.CharField()
    is_latest = django.db.models.BooleanField()

    class Meta:
        db_table = "dice_release"

def entry():
    orders = ErdaDeploymentOrder.objects.all()

    for order in orders:
        if order.type != 'PROJECT_RELEASE':
            continue
        try:
            release = DiceRelease.objects.get(release_id=order.release_id)
        except DiceRelease.DoesNotExist:
            print("release %s does not exist, skip the deployment order %s" % (order.release_id, order.id))
            continue
        order.deploy_list = release.application_release_list
        order.save()


entries: [callable] = [
    entry,
]
