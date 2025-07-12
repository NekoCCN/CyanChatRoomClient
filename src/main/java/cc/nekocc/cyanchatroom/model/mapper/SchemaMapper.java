package cc.nekocc.cyanchatroom.model.mapper;

public interface SchemaMapper
{
    void createConversationsTable();

    void createMessagesTable();

    void createMessagesIndex();
}