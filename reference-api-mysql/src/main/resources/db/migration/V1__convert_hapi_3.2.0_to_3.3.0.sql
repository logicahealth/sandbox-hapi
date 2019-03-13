--
--  * #%L
--  *
--  * %%
--  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
--  * %%
--  * Licensed under the Apache License, Version 2.0 (the "License");
--  * you may not use this file except in compliance with the License.
--  * You may obtain a copy of the License at
--  *
--  *      http://www.apache.org/licenses/LICENSE-2.0
--  *
--  * Unless required by applicable law or agreed to in writing, software
--  * distributed under the License is distributed on an "AS IS" BASIS,
--  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  * See the License for the specific language governing permissions and
--  * limitations under the License.
--  * #L%
--

ALTER TABLE HFJ_RESOURCE DROP COLUMN RES_TEXT;
ALTER TABLE HFJ_RESOURCE DROP COLUMN RES_ENCODING;
-- ALTER TABLE hfj_res_ver ALTER COLUMN res_encoding DROP NOT NULL;
ALTER TABLE HFJ_RES_VER MODIFY RES_ENCODING VARCHAR(5) NULL;
-- ALTER TABLE hfj_res_ver ALTER COLUMN res_text DROP NOT NULL;
ALTER TABLE HFJ_RES_VER MODIFY COLUMN RES_TEXT LONGBLOB NULL;
