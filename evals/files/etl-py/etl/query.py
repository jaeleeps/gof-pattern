class Query:
    def __init__(self, table, limit):
        self.table = table
        self.limit = limit


class QueryBuilder:
    def __init__(self):
        self._table = None
        self._limit = 100

    def table(self, name):
        self._table = name
        return self

    def limit(self, n):
        self._limit = n
        return self

    def build(self):
        return Query(self._table, self._limit)
