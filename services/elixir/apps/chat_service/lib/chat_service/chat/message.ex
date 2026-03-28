defmodule ChatService.Chat.Message do
  use Ecto.Schema
  import Ecto.Changeset

  @primary_key {:id, :binary_id, autogenerate: true}
  schema "chat_messages" do
    field :room_id, :binary_id
    field :user_id, :string
    field :body, :string
    field :message_type, :string, default: "text"
    field :metadata, :map, default: %{}

    timestamps(type: :utc_datetime)
  end

  def changeset(message, attrs) do
    message
    |> cast(attrs, [:room_id, :user_id, :body, :message_type, :metadata])
    |> validate_required([:room_id, :user_id, :body])
  end
end
