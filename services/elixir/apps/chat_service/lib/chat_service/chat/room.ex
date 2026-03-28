defmodule ChatService.Chat.Room do
  use Ecto.Schema
  import Ecto.Changeset

  @primary_key {:id, :binary_id, autogenerate: true}
  schema "chat_rooms" do
    field :tenant_id, :string
    field :name, :string
    field :room_type, :string, default: "support"
    field :participants, {:array, :string}, default: []

    timestamps(type: :utc_datetime)
  end

  def changeset(room, attrs) do
    room
    |> cast(attrs, [:tenant_id, :name, :room_type, :participants])
    |> validate_required([:tenant_id, :name])
  end
end
