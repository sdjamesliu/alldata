
import json

from job.model_template.utils import ModelTemplateDriver

DEBUG = False


class ESMMModelTemplateDriver(ModelTemplateDriver):
    def __init__(self, output_file=None, args=None, local=False):
        super(ESMMModelTemplateDriver, self).__init__("ESMM model template" + ("(debuging)" if DEBUG else ""),
                                                      output_file, args, local)


if __name__ == "__main__":
    if DEBUG:
        # import tensorflow as tf
        # tf.config.run_functions_eagerly(True)
        with open('./job_config_demo.json', 'r') as jcf:
            demo_job = json.load(jcf)
            driver = ESMMModelTemplateDriver(args=[
                "--job", json.dumps(demo_job),
                "--export-path", "./runs"
            ], local=True)
            driver.run()
    else:
        driver = ESMMModelTemplateDriver()
        driver.run()
