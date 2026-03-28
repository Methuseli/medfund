FROM golang:1.23-alpine AS build
WORKDIR /workspace/gateway
# Copy shared module (preserves ../shared relative path from go.mod replace)
COPY shared/ /workspace/shared/
# Copy service
COPY gateway/go.mod gateway/go.sum ./
RUN go mod download
COPY gateway/ .
RUN CGO_ENABLED=0 GOOS=linux go build -o /server ./cmd

FROM alpine:3.20
RUN apk --no-cache add ca-certificates
WORKDIR /app
COPY --from=build /server .
EXPOSE 3000
ENTRYPOINT ["./server"]
