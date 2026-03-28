FROM golang:1.23-alpine AS build
WORKDIR /workspace/payment-gateway
COPY shared/ /workspace/shared/
COPY payment-gateway/go.mod payment-gateway/go.sum ./
RUN go mod download
COPY payment-gateway/ .
RUN CGO_ENABLED=0 GOOS=linux go build -o /server ./cmd

FROM alpine:3.20
RUN apk --no-cache add ca-certificates
WORKDIR /app
COPY --from=build /server .
EXPOSE 3004
ENTRYPOINT ["./server"]
