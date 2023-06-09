
import json
import os

# Global caching for JSON language packs
ALL_LANGUAGE_PACKS = {"en": {}}

DIR = os.path.dirname(os.path.abspath(__file__))


def get_language_pack(locale):
    pack = ALL_LANGUAGE_PACKS.get(locale)
    if not pack:
        filename = DIR + "/{}/LC_MESSAGES/messages.json".format(locale)
        try:
            with open(filename) as f:
                pack = json.load(f)
                ALL_LANGUAGE_PACKS[locale] = pack
        except Exception:
            # Assuming english, client side falls back on english
            pass
    return pack






