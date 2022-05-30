DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'dota_user_id_seq' ) THEN CREATE SEQUENCE dota_user_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "dota_user" (
	"id" integer NOT NULL default nextval('dota_user_id_seq') PRIMARY KEY,
	"username" CHARACTER VARYING(75) NOT NULL,
	"enabled" boolean NOT NULL,
	"accountNonLocked" boolean NOT NULL,
	"accountNonExpired" boolean NOT NULL,
	"credentialsNonExpired" boolean NOT NULL,
	"lastLogin" timestamp with time zone NOT NULL,
	"dateJoined" timestamp with time zone NOT NULL
);

DO 
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'dota_user_unique_username' ) THEN 
	ALTER TABLE "dota_user" ADD CONSTRAINT dota_user_unique_username UNIQUE(username);
END IF;
END 
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'image_id_seq' ) THEN CREATE SEQUENCE image_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS image (
    "id" integer NOT NULL default nextval('image_id_seq') PRIMARY KEY,
    "fn" character varying(255),
    "sha1" character varying(40),
    "imageId" integer
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'image_image_id' ) THEN
	ALTER TABLE image ADD CONSTRAINT image_image_id FOREIGN KEY ("imageId") REFERENCES "image" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'player_id_seq' ) THEN CREATE SEQUENCE player_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS player (
    "id" integer NOT NULL default nextval('player_id_seq') PRIMARY KEY,
    "username" CHARACTER VARYING(75) NOT NULL,
    "steamId64" bigint NOT NULL,
    "personName" character varying(50) NOT NULL,
    "realName" character varying(50),
    "profileUrl" text NOT NULL,
    "imageId" integer,
    "personState" integer,
    "visibilityState" integer,
    "profileState" boolean,
    "playerClanId" bigint,
    "gameId" character varying(50),
    "countryCode" character varying(2),
    "cityId" character varying(50),
    "fetchTime" timestamp with time zone NOT NULL,
    "dateAdded" timestamp with time zone,
    "lastLogoff" timestamp with time zone
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_image_id' ) THEN
	ALTER TABLE player ADD CONSTRAINT player_image_id FOREIGN KEY ("imageId") REFERENCES "image" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'friendship_id_seq' ) THEN CREATE SEQUENCE friendship_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS friendship (
	"id" integer NOT NULL default nextval('friendship_id_seq') PRIMARY KEY,
	"fromPlayerId" integer NOT NULL,
	"toPlayerId" integer NOT NULL,
	UNIQUE ("fromPlayerId", "toPlayerId")
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'friendship_reference_from' ) THEN
	ALTER TABLE friendship ADD CONSTRAINT friendship_reference_from FOREIGN KEY ("fromPlayerId") REFERENCES "player" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'friendship_reference_to' ) THEN
	ALTER TABLE friendship ADD CONSTRAINT friendship_reference_to FOREIGN KEY ("toPlayerId") REFERENCES "player" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO 
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'attribute_id_seq' ) THEN CREATE SEQUENCE attribute_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "attribute" (
    "id" integer NOT NULL default nextval('attribute_id_seq') PRIMARY KEY,
    "name" text,
    "descriptionFormat" text,
    "descriptionString" text,
    "defindex" integer,
    "hidden" boolean,
    UNIQUE("defindex")
);

DO 
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'quality_id_seq' ) THEN CREATE SEQUENCE quality_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "itemQuality" (
    "id" integer NOT NULL default nextval('quality_id_seq') PRIMARY KEY,
    "key" integer NOT NULL,
    "value" character varying(20),
    "name" character varying(20)
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'item_id_seq' ) THEN CREATE SEQUENCE item_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS item (
    "id" integer NOT NULL default nextval('item_id_seq') PRIMARY KEY,
    "name" character varying(100),
    "defindex" integer,
    "typeDisplay" character varying(100),
    "description" text,
    "properName" boolean,
    "quality" integer,
    "qualityDisplay" character varying(20),
    "imageId" integer
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'item_image_id' ) THEN
	ALTER TABLE item ADD CONSTRAINT item_image_id FOREIGN KEY ("imageId") REFERENCES "image" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;


DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'player_item_id_seq' ) THEN CREATE SEQUENCE player_item_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'item_set_id_seq' ) THEN CREATE SEQUENCE item_set_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "itemSet" (
    "id" integer NOT NULL default nextval('item_set_id_seq') PRIMARY KEY,
    "name" character varying(100)
);

DO 
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'item_capability_id_seq' ) THEN CREATE SEQUENCE item_capability_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "itemCapability" (
    "id" integer NOT NULL default nextval('item_capability_id_seq') PRIMARY KEY,
    "name" character varying(100),
    "description" text
);

DO 
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'item_attached_capability_id_seq' ) THEN CREATE SEQUENCE item_attached_capability_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "itemAttachedCapability" (
	"id" integer NOT NULL default nextval('item_attached_capability_id_seq') PRIMARY KEY,
	"itemId" integer NOT NULL,
	"capabilityId" integer NOT NULL,
	UNIQUE ("itemId", "capabilityId")
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'attached_capability_item' ) THEN
	ALTER TABLE "itemAttachedCapability" ADD CONSTRAINT attached_capability_item FOREIGN KEY ("itemId") REFERENCES "item" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'attached_capability' ) THEN
	ALTER TABLE "itemAttachedCapability" ADD CONSTRAINT attached_capability FOREIGN KEY ("capabilityId") REFERENCES "itemCapability" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO 
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'item_attribute_id_seq' ) THEN CREATE SEQUENCE item_attribute_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "itemAttribute" (
    "id" integer NOT NULL default nextval('item_attribute_id_seq') PRIMARY KEY,
    "itemId" integer,
    "defindex" integer,
    "name" character varying(100),
    "value" character varying(20),
    "effectTypeDisplay" character varying(50),
    "hidden" boolean,
    "descriptionFormat" character varying(50),
    "descriptionString" text,
    UNIQUE("itemId", "defindex")
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'item_attribute_item_id' ) THEN
	ALTER TABLE "itemAttribute" ADD CONSTRAINT item_attribute_item_id FOREIGN KEY ("itemId") REFERENCES "item" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS "playerItem" (
    "id" integer NOT NULL default nextval('player_item_id_seq') PRIMARY KEY,
    "playerId" integer NOT NULL,
    "itemId" integer NOT NULL,
    "defindex" integer,
    "level" integer,
    "cannotTrade" boolean,
    "cannotCraft" boolean,
    "quality" integer,
    "qualityDisplay" character varying(20),
    "customName" text,
    "customDescription" text
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_item_player' ) THEN
	ALTER TABLE "playerItem" ADD CONSTRAINT player_item_player FOREIGN KEY ("playerId") REFERENCES "player" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_item' ) THEN
	ALTER TABLE "playerItem" ADD CONSTRAINT player_item FOREIGN KEY ("itemId") REFERENCES "item" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'player_item_attribute_id_seq' ) THEN CREATE SEQUENCE player_item_attribute_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS "playerItemAttribute" (
    "id" integer NOT NULL default nextval('player_item_attribute_id_seq') PRIMARY KEY,
    "itemId" integer,
    "value" text,
    "defindex" integer
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_item_attribute_item' ) THEN
	ALTER TABLE "playerItemAttribute" ADD CONSTRAINT player_item_attribute_item FOREIGN KEY ("itemId") REFERENCES "playerItem" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'player_offer_id_seq' ) THEN CREATE SEQUENCE player_offer_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END 
$do$;

CREATE TABLE IF NOT EXISTS "playerOffer" (
	"id" integer NOT NULL default nextval('player_offer_id_seq') PRIMARY KEY,
	"message" text,
	"dateAdded" timestamp with time zone NOT NULL
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'player_offer_item_id_seq' ) THEN CREATE SEQUENCE player_offer_item_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS "playerOfferItem" (
	"id" integer NOT NULL default nextval('player_offer_item_id_seq') PRIMARY KEY,
	"playerOfferId" integer,
	"playerItemId" integer
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_offer_item_player_offer_id' ) THEN
	ALTER TABLE "playerOfferItem" ADD CONSTRAINT player_offer_item_player_offer_id FOREIGN KEY ("playerOfferId") REFERENCES "playerOffer" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_offer_item_player_item_id' ) THEN
	ALTER TABLE "playerOfferItem" ADD CONSTRAINT player_offer_item_player_item_id FOREIGN KEY ("playerItemId") REFERENCES "playerItem" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'player_demand_item_id_seq' ) THEN CREATE SEQUENCE player_demand_item_id_seq START 1
	INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
END IF;
END
$do$;

CREATE TABLE IF NOT EXISTS "playerDemandItem" (
	"id" integer NOT NULL default nextval('player_demand_item_id_seq') PRIMARY KEY,
	"playerOfferId" integer,
	"demandedItemId" integer
);

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_demand_item_player_offer_id' ) THEN
	ALTER TABLE "playerDemandItem" ADD CONSTRAINT player_demand_item_player_offer_id FOREIGN KEY ("playerOfferId") REFERENCES "playerOffer" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;

DO
$do$
BEGIN IF NOT EXISTS (SELECT 0 FROM pg_constraint where conname = 'player_demanded_item_id' ) THEN
	ALTER TABLE "playerDemandItem" ADD CONSTRAINT player_demanded_item_id FOREIGN KEY ("demandedItemId") REFERENCES "item" ("id") DEFERRABLE INITIALLY DEFERRED;
END IF;
END
$do$;
