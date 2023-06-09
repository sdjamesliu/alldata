
class AddModelException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)


class QueryModelException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)


class UpdateModelException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)


class DeployModelException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)


class AddEmbeddingException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)


class QueryEmbeddingException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)


class DeleteEmbeddingException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)