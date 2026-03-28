FROM golang:1.23-alpine AS build
WORKDIR /app
# Copy shared module
COPY shared/ /shared/
# Copy service
COPY file-service/ /app/
RUN go mod download && CGO_ENABLED=0 GOOS=linux go build -o /server ./cmd

FROM alpine:3.20
RUN apk --no-cache add ca-certificates
WORKDIR /app
COPY --from=build /server .
EXPOSE 3003
ENTRYPOINT ["./server"]
