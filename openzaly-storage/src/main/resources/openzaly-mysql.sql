CREATE DATABASE IF NOT EXISTS openzaly;

use openzaly;

CREATE TABLE IF NOT EXISTS site_config_info(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, config_key INTEGER UNIQUE NOT NULL, config_value TEXT);

CREATE TABLE IF NOT EXISTS site_user_profile(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) UNIQUE NOT NULL,
            global_user_id VARCHAR(100) UNIQUE NOT NULL,
            user_id_pubk TEXT NOT NULL,
            site_login_id VARCHAR(50) UNIQUE,
            login_id_lowercase VARCHAR(50) UNIQUE,
            user_password VARCHAR(50),
            user_name VARCHAR(50) NOT NULL,
            user_name_in_latin VARCHAR(50),
            user_photo VARCHAR(100),
            phone_id VARCHAR(20),
            self_introduce VARCHAR(100),
            apply_info varchar(100),
            user_status INTEGER,
            mute BOOLEAN,
            register_time BIGINT);
            

CREATE TABLE IF NOT EXISTS site_user_session(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) not null,
            session_id VARCHAR(100), 
            is_online boolean, 
            device_id VARCHAR(50), 
            login_time BIGINT);
            
CREATE TABLE IF NOT EXISTS site_user_friend(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL,
            site_friend_id VARCHAR(50) NOT NULL,
            alias_name VARCHAR(50),
            alias_name_in_latin VARCHAR(50),
            relation INTEGER,
            mute BOOLEAN,
            add_time BIGINT);
            
CREATE TABLE IF NOT EXISTS site_friend_apply(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) not null,
            site_friend_id VARCHAR(50) not null,
            apply_reason VARCHAR(100),
            apply_time BIGINT);

CREATE TABLE IF NOT EXISTS site_user_message(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL, 
            msg_id VARCHAR(50), 
            send_user_id VARCHAR(50), 
            msg_type INTEGER, 
            content TEXT, 
            device_id VARCHAR(50), 
            ts_key TEXT, 
            msg_time BIGINT);

CREATE TABLE IF NOT EXISTS site_message_pointer(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) not null,
            pointer INTEGER, 
            device_id VARCHAR(50));

CREATE TABLE IF NOT EXISTS site_user_group(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) not null, 
            site_group_id VARCHAR(50) not null, 
            user_role INTEGER, 
            mute BOOLEAN, 
            add_time BIGINT);

CREATE TABLE IF NOT EXISTS site_group_profile(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_group_id VARCHAR(50) UNIQUE NOT NULL,
            create_user_id VARCHAR(50),
            group_name VARCHAR(50),
            group_photo VARCHAR(100),
            group_notice VARCHAR(100),
            ts_status INTEGER,
            group_status INTEGER,
            close_invite_group_chat BOOLEAN,
            create_time BIGINT);

CREATE TABLE IF NOT EXISTS site_group_message(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_group_id VARCHAR(50) NOT NULL, 
            msg_id VARCHAR(50), 
            send_user_id VARCHAR(50), 
            send_device_id VARCHAR(50), 
            msg_type INTEGER, 
            content TEXT, 
            msg_time BIGINT);

CREATE TABLE IF NOT EXISTS site_group_message_pointer(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) not null,
            site_group_id VARCHAR(50) not null,
            pointer INTEGER,
            device_id VARCHAR(50));

CREATE TABLE IF NOT EXISTS site_user_device(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL,
            device_id VARCHAR(50) UNIQUE NOT NULL,
            user_device_pubk TEXT NOT NULL,
            device_uuid VARCHAR(50) UNIQUE,
            user_token VARCHAR(50),
            device_name VARCHAR(60),
            device_ip VARCHAR(50),
            active_time BIGINT,
            add_time BIGINT);

CREATE TABLE IF NOT EXISTS site_plugin_manager(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
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

CREATE TABLE IF NOT EXISTS site_user_uic(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            uic VARCHAR(20) UNIQUE NOT NULL,
            site_user_id VARCHAR(50),
            status INTEGER,
            create_time BIGINT,
            use_time BIGINT);
            