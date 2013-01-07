
    create table Translation (
        id int4 not null,
        nameSource varchar(255),
        nameTarget varchar(255),
        primary key (id)
    );

    create table etype (
        id int4 not null,
        etype varchar(255),
        primary key (id)
    );

    create table fullname (
        id int4 not null,
        nGramCode varchar(255),
        name varchar(255),
        nameNormalized varchar(255),
        nameToCompare varchar(255),
        GUID int4 not null,
        primary key (id)
    );

    create table individualname (
        id int4 not null,
        frequency int4 not null,
        name varchar(255),
        primary key (id)
    );

    create table namedentity (
        GUID int4 not null,
        url varchar(255),
        etype_id int4 not null,
        primary key (GUID)
    );

    create table namefield (
        id int4 not null,
        fieldName varchar(255),
        etype_id int4 not null,
        primary key (id)
    );

    create table nametoken (
        id int4 not null,
        position int4 not null,
        full_name_id int4 not null,
        name_id int4 not null,
        field_id int4 not null,
        primary key (id),
        unique (full_name_id, name_id)
    );

    create table nametranslation (
        source_name_id int4 not null,
        target_name_id int4 not null,
        primary key (source_name_id, target_name_id)
    );

    create table triggerword (
        id int4 not null,
        triggerWord varchar(255),
        type_id int4 not null,
        primary key (id)
    );

    create table triggerwordetype (
        id int4 not null,
        etype_id int4 not null,
        trigger_word_id int4 not null,
        primary key (id)
    );

    create table triggerwordtoken (
        id int4 not null,
        position int4 not null,
        full_name_id int4 not null,
        trigger_word_id int4 not null,
        primary key (id),
        unique (full_name_id, trigger_word_id)
    );

    create table triggerwordtype (
        id int4 not null,
        comparable bool not null,
        type varchar(255),
        primary key (id)
    );

    create table triggerwordvariations (
        source_tw_id int4 not null,
        target_tw_id int4 not null,
        primary key (source_tw_id, target_tw_id)
    );

    alter table fullname 
        add constraint FK4F61BD9A6AF229B9 
        foreign key (GUID) 
        references namedentity;

    alter table namedentity 
        add constraint FK39FAC5CD1A0EF2E 
        foreign key (etype_id) 
        references etype;

    alter table namefield 
        add constraint FK49E9054FD1A0EF2E 
        foreign key (etype_id) 
        references etype;

    alter table nametoken 
        add constraint FK4AB11E4E94E0525 
        foreign key (full_name_id) 
        references fullname;

    alter table nametoken 
        add constraint FK4AB11E4EB72AA363 
        foreign key (field_id) 
        references namefield;

    alter table nametoken 
        add constraint FK4AB11E4E9B8B1CDF 
        foreign key (name_id) 
        references individualname;

    alter table nametranslation 
        add constraint FK5A7B0346C55B1431 
        foreign key (target_name_id) 
        references individualname;

    alter table nametranslation 
        add constraint FK5A7B0346D2FAE0BB 
        foreign key (source_name_id) 
        references individualname;

    alter table triggerword 
        add constraint FKD77980C287291830 
        foreign key (type_id) 
        references triggerwordtype;

    alter table triggerwordetype 
        add constraint FKA9487D9DD1A0EF2E 
        foreign key (etype_id) 
        references etype;

    alter table triggerwordetype 
        add constraint FKA9487D9DE47A8A7F 
        foreign key (trigger_word_id) 
        references triggerword;

    alter table triggerwordtoken 
        add constraint FKAA19627794E0525 
        foreign key (full_name_id) 
        references fullname;

    alter table triggerwordtoken 
        add constraint FKAA196277E47A8A7F 
        foreign key (trigger_word_id) 
        references triggerword;

    alter table triggerwordvariations 
        add constraint FK2B7F9702D8C92AC9 
        foreign key (source_tw_id) 
        references triggerword;

    alter table triggerwordvariations 
        add constraint FK2B7F9702696BC3BF 
        foreign key (target_tw_id) 
        references triggerword;
