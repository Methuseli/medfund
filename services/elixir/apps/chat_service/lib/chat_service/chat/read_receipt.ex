defmodule ChatService.Chat.ReadReceipt do
  use Ecto.Schema
  import Ecto.Changeset

  @primary_key {:id, :binary_id, autogenerate: true}
  schema "chat_read_receipts" do
    field :room_id, :binary_id
    field :user_id, :string
    field :last_read_message_id, :binary_id
    field :last_read_at, :utc_datetime

    timestamps(type: :utc_datetime)
  end

  def changeset(receipt, attrs) do
    receipt
    |> cast(attrs, [:room_id, :user_id, :last_read_message_id, :last_read_at])
    |> validate_required([:room_id, :user_id])
    |> unique_constraint([:room_id, :user_id])
  end
end
