from pydantic import BaseModel, Field


class ValuesRequest(BaseModel):
    codes: list[str] = Field(default_factory=list)


class NamesRequest(BaseModel):
    names: list[str] = Field(default_factory=list)


class PointsRequest(BaseModel):
    points: list[dict] = Field(default_factory=list, max_length=500)
