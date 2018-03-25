DROP TABLE IF EXISTS OFFENDER_KEY_WORKER;

CREATE TABLE OFFENDER_KEY_WORKER
(
  OFFENDER_KEYWORKER_ID            BIGSERIAL    NOT NULL,

  OFFENDER_NO                   VARCHAR( 10)    NOT NULL,
  STAFF_ID                            BIGINT    NOT NULL,
  ASSIGNED_DATE_TIME               TIMESTAMP    NOT NULL,

  ACTIVE_FLAG                      CHAR(  1)    NOT NULL,
  ALLOC_REASON                  VARCHAR( 12)    NOT NULL,
  ALLOC_TYPE                       CHAR(  1)    NOT NULL,
  USER_ID                       VARCHAR( 32)    NOT NULL,
  AGY_LOC_ID                    VARCHAR(  6)    NOT NULL,

  EXPIRY_DATE_TIME                 TIMESTAMP,
  DEALLOC_REASON                VARCHAR( 12),

  CREATE_DATETIME                  TIMESTAMP    NOT NULL,
  CREATE_USER_ID                VARCHAR( 32)    NOT NULL,

  MODIFY_DATETIME                  TIMESTAMP,
  MODIFY_USER_ID                VARCHAR( 32),

  CONSTRAINT OFFENDER_KEY_WORKER_PK PRIMARY KEY (OFFENDER_KEYWORKER_ID),

  CONSTRAINT OFFENDER_KEYWORKER_UNIQUE UNIQUE (OFFENDER_NO, STAFF_ID, ASSIGNED_DATE_TIME),

  CONSTRAINT OFFENDER_KEY_WORKER_C1 CHECK (ALLOC_TYPE IN ('A','M','P'))
);

COMMENT ON TABLE OFFENDER_KEY_WORKER IS 'Records the Key Worker assignment history of offenders on remand or serving custodial sentences held within an establishment.';

COMMENT ON COLUMN OFFENDER_KEY_WORKER.OFFENDER_NO        IS 'The Related Offender No';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.STAFF_ID           IS 'The Related Key Worker Staff Id';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.ASSIGNED_DATE_TIME IS 'Assigned Date and Time';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.USER_ID            IS 'Assigned by User Id';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.AGY_LOC_ID         IS 'Establishment Id';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.ACTIVE_FLAG        IS 'Assignment Active Flag';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.EXPIRY_DATE_TIME   IS 'Expiry Date and Time of Assignment';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.ALLOC_REASON       IS 'Reason for allocation';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.DEALLOC_REASON     IS 'Reason for de-allocation';
COMMENT ON COLUMN OFFENDER_KEY_WORKER.ALLOC_TYPE         IS 'Type of allocation, M for manual, A for auto, P for provisional';
