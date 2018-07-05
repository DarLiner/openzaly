
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS site_config_info(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            config_key INTEGER UNIQUE NOT NULL,
            config_value TEXT
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '站点配置表';

CREATE TABLE IF NOT EXISTS site_user_profile(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) UNIQUE NOT NULL,
            global_user_id VARCHAR(100) UNIQUE NOT NULL,
            site_login_id VARCHAR(50) UNIQUE,
            login_id_lowercase VARCHAR(50) UNIQUE,
            phone_id VARCHAR(20) UNIQUE,
            user_password VARCHAR(50),
            user_id_pubk TEXT NOT NULL,
            user_name VARCHAR(50) NOT NULL,
            user_name_in_latin VARCHAR(50),
            user_photo VARCHAR(100),
            self_introduce VARCHAR(100),
            apply_info varchar(100),
            user_status INTEGER,
            mute BOOLEAN,
            register_time BIGINT
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户资料表';
            
ALTER TABLE site_user_profile CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE site_user_profile MODIFY COLUMN user_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
            

CREATE TABLE IF NOT EXISTS site_user_session(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            session_id VARCHAR(100) UNIQUE, 
            site_user_id VARCHAR(50) NOT NULL,
            is_online boolean, 
            device_id VARCHAR(50), 
            login_time BIGINT,
            INDEX (site_user_id,device_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户会话表';
            
CREATE TABLE IF NOT EXISTS site_user_friend(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL,
            site_friend_id VARCHAR(50) NOT NULL,
            alias_name VARCHAR(50),
            alias_name_in_latin VARCHAR(50),
            relation INTEGER,
            mute BOOLEAN,
            add_time BIGINT,
            UNIQUE INDEX(site_user_id,site_friend_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户好友关系表';
            
CREATE TABLE IF NOT EXISTS site_friend_apply(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL,
            site_friend_id VARCHAR(50) NOT NULL,
            apply_reason VARCHAR(100),
            apply_time BIGINT,
            INDEX(site_user_id,site_friend_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '好友申请表';

CREATE TABLE IF NOT EXISTS site_user_message(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL, 
            msg_id VARCHAR(50) UNIQUE NOT NULL, 
            send_user_id VARCHAR(50), 
            receive_user_id VARCHAR(50),
            msg_type INTEGER, 
            content TEXT, 
            device_id VARCHAR(50), 
            ts_key TEXT, 
            msg_time BIGINT,
            INDEX(site_user_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '用户消息表';
            
ALTER TABLE site_user_message CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE site_user_message MODIFY COLUMN content text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS site_message_pointer(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL,
            device_id VARCHAR(50),
            pointer INTEGER, 
            INDEX(site_user_id,device_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户消息游标表';

CREATE TABLE IF NOT EXISTS site_user_group(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) not null, 
            site_group_id VARCHAR(50) not null, 
            user_role INTEGER, 
            mute BOOLEAN, 
            add_time BIGINT,
            UNIQUE INDEX(site_user_id,site_group_id),
            INDEX(site_group_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户群组列表';

CREATE TABLE IF NOT EXISTS site_group_profile(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_group_id VARCHAR(50) UNIQUE NOT NULL,
            create_user_id VARCHAR(50),
            group_name VARCHAR(50),
            group_photo VARCHAR(100),
            group_notice VARCHAR(100),
            ts_status INTEGER,
            group_status INTEGER,
            close_invite_group_chat BOOLEAN,
            create_time BIGINT,
            INDEX (site_group_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '群组资料表';
            
ALTER TABLE site_group_profile CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE site_group_profile MODIFY COLUMN group_name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;            

CREATE TABLE IF NOT EXISTS site_group_message(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_group_id VARCHAR(50) NOT NULL, 
            msg_id VARCHAR(50) UNIQUE NOT NULL, 
            send_user_id VARCHAR(50), 
            send_device_id VARCHAR(50), 
            msg_type INTEGER, 
            content TEXT, 
            msg_time BIGINT,
            INDEX(site_group_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '群组消息表';
            
ALTER TABLE site_group_message CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE site_group_message MODIFY COLUMN content text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
            

CREATE TABLE IF NOT EXISTS site_group_message_pointer(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_group_id VARCHAR(50) not null,
            site_user_id VARCHAR(50) not null,
            device_id VARCHAR(50),
            pointer INTEGER,
            INDEX(site_group_id,site_user_id,device_id)            
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '群组消息游标表';

CREATE TABLE IF NOT EXISTS site_user_device(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            site_user_id VARCHAR(50) NOT NULL,
            device_id VARCHAR(50) UNIQUE NOT NULL,
            user_device_pubk TEXT NOT NULL,
            device_uuid VARCHAR(50) UNIQUE,
            user_token VARCHAR(50),
            device_name VARCHAR(60),
            device_ip VARCHAR(50),
            active_time BIGINT,
            add_time BIGINT,
            UNIQUE INDEX(site_user_id,device_id)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户设备表';

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
            add_time BIGINT
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '站点扩展表';

CREATE TABLE IF NOT EXISTS site_user_uic(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            uic VARCHAR(20) UNIQUE NOT NULL,
            site_user_id VARCHAR(50),
            status INTEGER,
            create_time BIGINT,
            use_time BIGINT
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '站点邀请码表';
            
CREATE TABLE IF NOT EXISTS site_expire_token(id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            token VARCHAR(100) UNIQUE NOT NULL,
            bid VARCHAR(100),       -- 业务id，siteUserId | siteGroupId
            btype INTEGER,          -- 业务类型，群组邀请码等
            status INTEGER,         -- 是否可用状态
            content TEXT,       
            create_time BIGINT,
            expire_time BIGINT,
            INDEX(token)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '站点令牌表';          
            
