SELECT cd.conversation_id                 AS "id",
       CONCAT(mi.company, ' ', mi."name") AS model,
       cd."role",
       cd.token,
       cdc."content"
FROM t_conversation_detail cd
         LEFT JOIN t_conversation_detail_content cdc ON cd.content_id = cdc."id"
         LEFT JOIN t_model_info AS mi ON cd.model_id = mi."id"
ORDER BY cd."id"