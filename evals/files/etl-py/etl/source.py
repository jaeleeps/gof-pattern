import json
import urllib.request

from .retry import retry
from .settings import settings


@retry(times=4)
def _fetch_page(cursor):
    url = f"{settings.api_base}/records?limit={settings.page_size}"
    if cursor:
        url += f"&cursor={cursor}"
    with urllib.request.urlopen(url) as resp:
        return json.load(resp)


def read_records():
    cursor = None
    while True:
        page = _fetch_page(cursor)
        yield from page["items"]
        cursor = page.get("next")
        if not cursor:
            return
