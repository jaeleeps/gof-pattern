import sys

from .exporters import CsvExporter, JsonLinesExporter
from .query import QueryBuilder
from .source import read_records
from .transform import by_email, by_id, dedupe

EXPORTERS = {"csv": CsvExporter, "jsonl": JsonLinesExporter}


def main(fmt="csv", dedupe_on="id"):
    q = QueryBuilder().table("customers").limit(500).build()
    key = by_email if dedupe_on == "email" else by_id
    records = dedupe(read_records(), key=key)
    path, n = EXPORTERS[fmt]().export(q.table, records)
    print(f"wrote {n} records to {path}")


if __name__ == "__main__":
    main(*sys.argv[1:])
