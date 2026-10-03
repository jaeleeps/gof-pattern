import csv
import json
import os
from abc import ABC, abstractmethod

from .settings import settings


class BaseExporter(ABC):
    extension = ""

    def export(self, name, records):
        path = os.path.join(settings.output_dir, f"{name}.{self.extension}")
        os.makedirs(settings.output_dir, exist_ok=True)
        with open(path, "w", newline="") as fh:
            self._begin(fh)
            count = 0
            for r in records:
                self._write(fh, r, count)
                count += 1
            self._end(fh)
        return path, count

    def _begin(self, fh):
        pass

    @abstractmethod
    def _write(self, fh, record, index):
        ...

    def _end(self, fh):
        pass


class CsvExporter(BaseExporter):
    extension = "csv"
    fields = ("id", "email", "name")

    def _begin(self, fh):
        self._w = csv.DictWriter(fh, fieldnames=self.fields, extrasaction="ignore")
        self._w.writeheader()

    def _write(self, fh, record, index):
        self._w.writerow(record)


class JsonLinesExporter(BaseExporter):
    extension = "jsonl"

    def _write(self, fh, record, index):
        fh.write(json.dumps(record) + "\n")
