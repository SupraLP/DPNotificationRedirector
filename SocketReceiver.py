import socket

connPort = 9002
bufferSize = 1024
address = "0.0.0.0"

receivingSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
receivingSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
receivingSocket.bind((address, connPort))
print("finished setting up socket")

print("waiting on connection")
receivingSocket.listen(4)
conn, (ip, port) = receivingSocket.accept()

print("waiting for messages on " + address, port)
while True:
    data = conn.recv(bufferSize)
    if data:
        print(data)
