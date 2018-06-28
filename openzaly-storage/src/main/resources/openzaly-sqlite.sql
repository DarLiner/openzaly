CREATE TABLE IF NOT EXISTS site_config_info(id INTEGER PRIMARY KEY NOT NULL,
            config_key INTEGER UNIQUE NOT NULL,
            config_value TEXT);

CREATE TABLE IF NOT EXISTS site_user_profile(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) UNIQUE NOT NULL,
            global_user_id VARCHAR(100) UNIQUE NOT NULL,
            site_login_id VARCHAR(50) UNIQUE,
            login_id_lowercase VARCHAR(50) UNIQUE,
            user_password VARCHAR(50),
            user_id_pubk TEXT NOT NULL,
            user_name VARCHAR(50) NOT NULL,
            user_name_in_latin VARCHAR(50),
            user_photo VARCHAR(100),
            phone_id VARCHAR(20),
            self_introduce VARCHAR(100),
            apply_info varchar(100),
            user_status INTEGER,
            mute BOOLEAN,
            register_time BIGINT);
            

CREATE TABLE IF NOT EXISTS site_user_session(id INTEGER PRIMARY KEY NOT NULL,
            session_id VARCHAR(100) UNIQUE, 
            site_user_id VARCHAR(50) NOT NULL,
            is_online boolean, 
            device_id VARCHAR(50), 
            login_time BIGINT);
            
CREATE TABLE IF NOT EXISTS site_user_friend(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) NOT NULL,
            site_friend_id VARCHAR(50) NOT NULL,
            alias_name VARCHAR(50),
            alias_name_in_latin VARCHAR(50),
            relation INTEGER,
            mute BOOLEAN,
            add_time BIGINT);
            
CREATE TABLE IF NOT EXISTS site_friend_apply(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) NOT NULL,
            site_friend_id VARCHAR(50) NOT NULL,
            apply_reason VARCHAR(100),
            apply_time BIGINT);

CREATE TABLE IF NOT EXISTS site_user_message(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) NOT NULL, 
            msg_id VARCHAR(50) UNIQUE NOT NULL, 
            send_user_id VARCHAR(50), 
            receive_user_id VARCHAR(50),
            msg_type INTEGER, 
            content TEXT, 
            device_id VARCHAR(50), 
            ts_key TEXT, 
            msg_time BIGINT);

CREATE TABLE IF NOT EXISTS site_message_pointer(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) NOT NULL,
            device_id VARCHAR(50),
            pointer INTEGER);

CREATE TABLE IF NOT EXISTS site_user_group(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) not null, 
            site_group_id VARCHAR(50) not null, 
            user_role INTEGER, 
            mute BOOLEAN, 
            add_time BIGINT);

CREATE TABLE IF NOT EXISTS site_group_profile(id INTEGER PRIMARY KEY NOT NULL,
            site_group_id VARCHAR(50) UNIQUE NOT NULL,
            create_user_id VARCHAR(50),
            group_name VARCHAR(50),
            group_photo VARCHAR(100),
            group_notice VARCHAR(100),
            ts_status INTEGER,
            group_status INTEGER,
            close_invite_group_chat BOOLEAN,
            create_time BIGINT);

CREATE TABLE IF NOT EXISTS site_group_message(id INTEGER PRIMARY KEY NOT NULL,
            site_group_id VARCHAR(50) NOT NULL, 
            msg_id VARCHAR(50) UNIQUE NOT NULL, 
            send_user_id VARCHAR(50), 
            send_device_id VARCHAR(50), 
            msg_type INTEGER, 
            content TEXT, 
            msg_time BIGINT);

CREATE TABLE IF NOT EXISTS site_group_message_pointer(id INTEGER PRIMARY KEY NOT NULL,
            site_group_id VARCHAR(50) not null,
            site_user_id VARCHAR(50) not null,
            device_id VARCHAR(50),
            pointer INTEGER);

CREATE TABLE IF NOT EXISTS site_user_device(id INTEGER PRIMARY KEY NOT NULL,
            site_user_id VARCHAR(50) NOT NULL,
            device_id VARCHAR(50) UNIQUE NOT NULL,
            user_device_pubk TEXT NOT NULL,
            device_uuid VARCHAR(50) UNIQUE,
            user_token VARCHAR(50),
            device_name VARCHAR(60),
            device_ip VARCHAR(50),
            active_time BIGINT,
            add_time BIGINT);

CREATE TABLE IF NOT EXISTS site_plugin_manager(id INTEGER PRIMARY KEY NOT NULL,
            name VARCHAR(50) UNIQUE NOT NULL,
            icon VARCHAR(100) NOT NULL,
            api_url TEXT,
            url_page TEXT,
            auth_key VARCHAR(50) NOT NULL,
            allowed_ip TEXT,
            position INTEGER,
            sort INTEGER,
            display_mode INTEGER,
            permission_status INTEGER,
            add_time BIGINT);

CREATE TABLE IF NOT EXISTS site_user_uic(id INTEGER PRIMARY KEY NOT NULL,
            uic VARCHAR(20) UNIQUE NOT NULL,
            site_user_id VARCHAR(50),
            status INTEGER,
            create_time BIGINT,
            use_time BIGINT);
            
CREATE TABLE IF NOT EXISTS site_expire_token(id INTEGER PRIMARY KEY NOT NULL,
            token VARCHAR(100) UNIQUE NOT NULL,
            btype INTEGER,
            status INTEGER,
            create_time BIGINT,
            expire_time BIGINT);          

            
-- add index 

--CREATE UNIQUE INDEX IF NOT EXISTS index_user_profile_id ON site_user_profile(site_user_id,global_user_id);
    
CREATE INDEX IF NOT EXISTS index_user_session ON site_user_session(site_user_id,device_id);
    
CREATE INDEX IF NOT EXISTS index_user_sessionid ON site_user_session(session_id);
    
CREATE UNIQUE INDEX IF NOT EXISTS index_user_friend ON site_user_friend(site_user_id,site_friend_id);
    
CREATE INDEX IF NOT EXISTS index_friend_apply ON site_friend_apply(site_user_id,site_friend_id);
    
CREATE INDEX IF NOT EXISTS index_u2_message ON site_user_message(site_user_id);
    
CREATE INDEX IF NOT EXISTS index_u2_pointer ON site_message_pointer(site_user_id,device_id);
    
CREATE UNIQUE INDEX IF NOT EXISTS index_user_group ON site_user_group(site_user_id,site_group_id);
    
CREATE INDEX IF NOT EXISTS index_group_profile ON site_group_profile(site_group_id);
    
CREATE INDEX IF NOT EXISTS index_group_message ON site_group_message(site_group_id);
    
CREATE INDEX IF NOT EXISTS index_group_pointer ON site_group_message_pointer(site_group_id,site_user_id,device_id);
    
CREATE UNIQUE INDEX IF NOT EXISTS index_user_device ON site_user_device(site_user_id,device_id);
     
CREATE INDEX IF NOT EXISTS index_expire_token ON site_expire_token(token);
