FROM python:latest

WORKDIR /app
ADD ./neural-net /app
RUN pip install -r requirements.txt

CMD ["python", "train.py"]
