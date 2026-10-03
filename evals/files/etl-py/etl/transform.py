def by_email(record):
    return record["email"].strip().lower()


def by_id(record):
    return record["id"]


def dedupe(records, key=by_id):
    seen = set()
    for r in records:
        k = key(r)
        if k in seen:
            continue
        seen.add(k)
        yield r
