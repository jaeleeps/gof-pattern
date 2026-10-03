import functools
import time


def retry(times=3, delay=0.5):
    def wrap(fn):
        @functools.wraps(fn)
        def inner(*args, **kwargs):
            for attempt in range(times):
                try:
                    return fn(*args, **kwargs)
                except IOError:
                    if attempt == times - 1:
                        raise
                    time.sleep(delay * (2 ** attempt))
        return inner
    return wrap
