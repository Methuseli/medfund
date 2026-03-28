defmodule ChatService.Chat do
  @moduledoc "Chat context — CRUD for rooms, messages, read receipts."

  import Ecto.Query
  alias ChatService.Repo
  alias ChatService.Chat.{Room, Message, ReadReceipt}

  # Rooms

  def list_rooms(tenant_id) do
    Room
    |> where([r], r.tenant_id == ^tenant_id)
    |> order_by([r], desc: r.inserted_at)
    |> Repo.all()
  end

  def get_room(id), do: Repo.get(Room, id)

  def create_room(attrs) do
    %Room{}
    |> Room.changeset(attrs)
    |> Repo.insert()
  end

  # Messages

  def list_messages(room_id, opts \\ []) do
    limit = Keyword.get(opts, :limit, 50)

    Message
    |> where([m], m.room_id == ^room_id)
    |> order_by([m], desc: m.inserted_at)
    |> limit(^limit)
    |> Repo.all()
    |> Enum.reverse()
  end

  def create_message(attrs) do
    %Message{}
    |> Message.changeset(attrs)
    |> Repo.insert()
  end

  # Read Receipts

  def upsert_read_receipt(room_id, user_id, message_id) do
    now = DateTime.utc_now() |> DateTime.truncate(:second)

    %ReadReceipt{}
    |> ReadReceipt.changeset(%{
      room_id: room_id,
      user_id: user_id,
      last_read_message_id: message_id,
      last_read_at: now
    })
    |> Repo.insert(
      on_conflict: [set: [last_read_message_id: message_id, last_read_at: now]],
      conflict_target: [:room_id, :user_id]
    )
  end

  def get_unread_count(room_id, user_id) do
    receipt = Repo.one(
      from rr in ReadReceipt,
        where: rr.room_id == ^room_id and rr.user_id == ^user_id
    )

    case receipt do
      nil ->
        Repo.aggregate(
          from(m in Message, where: m.room_id == ^room_id),
          :count
        )
      %{last_read_at: last_read_at} ->
        Repo.aggregate(
          from(m in Message,
            where: m.room_id == ^room_id and m.inserted_at > ^last_read_at),
          :count
        )
    end
  end
end
