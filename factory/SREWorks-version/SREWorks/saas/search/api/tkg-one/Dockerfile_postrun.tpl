FROM {{ POSTRUN_IMAGE }}
COPY ./APP-META-PRIVATE/postrun /app/postrun
ENTRYPOINT ["sleep","999999999"]
