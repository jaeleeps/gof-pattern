import os
from dataclasses import dataclass


@dataclass(frozen=True)
class Settings:
    api_base: str
    page_size: int
    output_dir: str


settings = Settings(
    api_base=os.environ.get("ETL_API_BASE", "https://api.example.com"),
    page_size=int(os.environ.get("ETL_PAGE_SIZE", "100")),
    output_dir=os.environ.get("ETL_OUTPUT_DIR", "./out"),
)
