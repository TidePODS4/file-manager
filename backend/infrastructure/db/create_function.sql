CREATE OR REPLACE FUNCTION get_full_path(dir_id UUID)
    RETURNS TEXT AS $$
DECLARE
result TEXT;
BEGIN
WITH RECURSIVE path_cte(id, parent_id, owner_id, full_path) AS (
    SELECT id, parent_id, owner_id, CAST(owner_id AS TEXT) || '/' || CAST(id AS TEXT)
    FROM file_metadata
    WHERE parent_id IS NULL
    UNION ALL
    SELECT d.id, d.parent_id, d.owner_id,
           pc.full_path || '/' || d.id::TEXT
    FROM file_metadata d
             INNER JOIN path_cte pc ON pc.id = d.parent_id
)
SELECT full_path INTO result FROM path_cte WHERE id = dir_id;
RETURN result;
END;
$$ LANGUAGE plpgsql;
