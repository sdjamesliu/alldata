FROM python:3.7

RUN wget https://github.com/stern/stern/releases/download/v1.21.0/stern_1.21.0_linux_amd64.tar.gz && tar -zxvf stern_1.21.0_linux_amd64.tar.gz && rm stern_1.21.0_linux_amd64.tar.gz && chmod +x stern &&  mv stern /usr/bin/stern
RUN apt update && apt install -y --force-yes --no-install-recommends vim apt-transport-https gnupg2 ca-certificates-java rsync jq  wget git dnsutils iputils-ping net-tools curl locales zip software-properties-common && apt-get autoclean

RUN python -m pip install --upgrade pip && \
    pip install requests -i https://mirrors.tencent.com/pypi/simple && \
    pip install kubernetes -i https://mirrors.tencent.com/pypi/simple && pip install psutil pysnooper

COPY job/pkgs /app/job/pkgs
COPY job/tf_distributed_evaluation/*.py /app/job/tf_distributed_evaluation/

WORKDIR /app
ENTRYPOINT ["python", "-m", "job.tf_distributed_evaluation.tfjob_launcher"]