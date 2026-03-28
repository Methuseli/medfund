FROM golang:1.23-alpine AS build
WORKDIR /workspace/audit-service
COPY shared/ /workspace/shared/
COPY audit-service/go.mod audit-service/go.sum ./
RUN go mod download
COPY audit-service/ .
RUN CGO_ENABLED=0 GOOS=linux go build -o /server ./cmd

FROM alpine:3.20
RUN apk --no-cache add ca-certificates
WORKDIR /app
COPY --from=build /server .
EXPOSE 3002
ENTRYPOINT ["./server"]
