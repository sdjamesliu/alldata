/* -*- mode: c; c-basic-offset: 4; indent-tabs-mode: nil -*- */
/* lib/crypto/crypto_tests/t_decrypt.c - Test decrypting known ciphertexts */
/*
 * Copyright (C) 2010 by the Massachusetts Institute of Technology.
 * All rights reserved.
 *
 * Export of this software from the United States of America may
 *   require a specific license from the United States Government.
 *   It is the responsibility of any person or organization contemplating
 *   export to obtain such a license before exporting.
 *
 * WITHIN THAT CONSTRAINT, permission to use, copy, modify, and
 * distribute this software and its documentation for any purpose and
 * without fee is hereby granted, provided that the above copyright
 * notice appear in all copies and that both that copyright notice and
 * this permission notice appear in supporting documentation, and that
 * the name of M.I.T. not be used in advertising or publicity pertaining
 * to distribution of the software without specific, written prior
 * permission.  Furthermore if you modify this software you must label
 * your software as modified software and not distribute it in such a
 * fashion that it might be confused with the original M.I.T. software.
 * M.I.T. makes no representations about the suitability of
 * this software for any purpose.  It is provided "as is" without express
 * or implied warranty.
 */

/*
 * This harness decrypts known ciphertexts to detect changes in encryption code
 * which are self-compatible but not compatible across versions.  With the -g
 * flag, the program generates a set of test cases.
 */

#include "k5-int.h"

struct test {
    krb5_enctype enctype;
    krb5_data plaintext;
    krb5_keyusage usage;
    krb5_data keybits;
    krb5_data ciphertext;
} test_cases[] = {
    {
        ENCTYPE_DES3_CBC_SHA1,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 24,
          "\x7A\x25\xDF\x89\x92\x29\x6D\xCE\xDA\x0E\x13\x5B\xC4\x04\x6E\x23"
          "\x75\xB3\xC1\x4C\x98\xFB\xC1\x62" },
        { KV5M_DATA, 28,
          "\x54\x8A\xF4\xD5\x04\xF7\xD7\x23\x30\x3F\x12\x17\x5F\xE8\x38\x6B"
          "\x7B\x53\x35\xA9\x67\xBA\xD6\x1F\x3B\xF0\xB1\x43" }
    },
    {
        ENCTYPE_DES3_CBC_SHA1,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 24,
          "\xBC\x07\x83\x89\x15\x13\xD5\xCE\x57\xBC\x13\x8F\xD3\xC1\x1A\xE6"
          "\x40\x45\x23\x85\x32\x29\x62\xB6" },
        { KV5M_DATA, 36,
          "\x9C\x3C\x1D\xBA\x47\x47\xD8\x5A\xF2\x91\x6E\x47\x45\xF2\xDC\xE3"
          "\x80\x46\x79\x6E\x51\x04\xBC\xCD\xFB\x66\x9A\x91\xD4\x4B\xC3\x56"
          "\x66\x09\x45\xC7" }
    },
    {
        ENCTYPE_DES3_CBC_SHA1,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 24,
          "\x2F\xD0\xF7\x25\xCE\x04\x10\x0D\x2F\xC8\xA1\x80\x98\x83\x1F\x85"
          "\x0B\x45\xD9\xEF\x85\x0B\xD9\x20" },
        { KV5M_DATA, 44,
          "\xCF\x91\x44\xEB\xC8\x69\x79\x81\x07\x5A\x8B\xAD\x8D\x74\xE5\xD7"
          "\xD5\x91\xEB\x7D\x97\x70\xC7\xAD\xA2\x5E\xE8\xC5\xB3\xD6\x94\x44"
          "\xDF\xEC\x79\xA5\xB7\xA0\x14\x82\xD9\xAF\x74\xE6" }
    },
    {
        ENCTYPE_DES3_CBC_SHA1,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 24,
          "\x0D\xD5\x20\x94\xE0\xF4\x1C\xEC\xCB\x5B\xE5\x10\xA7\x64\xB3\x51"
          "\x76\xE3\x98\x13\x32\xF1\xE5\x98" },
        { KV5M_DATA, 44,
          "\x83\x9A\x17\x08\x1E\xCB\xAF\xBC\xDC\x91\xB8\x8C\x69\x55\xDD\x3C"
          "\x45\x14\x02\x3C\xF1\x77\xB7\x7B\xF0\xD0\x17\x7A\x16\xF7\x05\xE8"
          "\x49\xCB\x77\x81\xD7\x6A\x31\x6B\x19\x3F\x8D\x30" }
    },
    {
        ENCTYPE_DES3_CBC_SHA1,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 24,
          "\xF1\x16\x86\xCB\xBC\x9E\x23\xEA\x54\xFE\xCD\x2A\x3D\xCD\xFB\x20"
          "\xB6\xFE\x98\xBF\x26\x45\xC4\xC4" },
        { KV5M_DATA, 60,
          "\x89\x43\x3E\x83\xFD\x0E\xA3\x66\x6C\xFF\xCD\x18\xD8\xDE\xEB\xC5"
          "\x3B\x9A\x34\xED\xBE\xB1\x59\xD9\xF6\x67\xC6\xC2\xB9\xA9\x64\x40"
          "\x1D\x55\xE7\xE9\xC6\x8D\x64\x8D\x65\xC3\xAA\x84\xFF\xA3\x79\x0C"
          "\x14\xA8\x64\xDA\x80\x73\xA9\xA9\x5C\x4B\xA2\xBC" }
    },

    {
        ENCTYPE_ARCFOUR_HMAC,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 16,
          "\xF8\x1F\xEC\x39\x25\x5F\x57\x84\xE8\x50\xC4\x37\x7C\x88\xBD\x85" },
        { KV5M_DATA, 24,
          "\x02\xC1\xEB\x15\x58\x61\x44\x12\x2E\xC7\x17\x76\x3D\xD3\x48\xBF"
          "\x00\x43\x4D\xDC\x65\x85\x95\x4C" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 16,
          "\x67\xD1\x30\x0D\x28\x12\x23\x86\x7F\x96\x47\xFF\x48\x72\x12\x73" },
        { KV5M_DATA, 25,
          "\x61\x56\xE0\xCC\x04\xE0\xA0\x87\x4F\x9F\xDA\x00\x8F\x49\x8A\x7A"
          "\xDB\xBC\x80\xB7\x0B\x14\xDD\xDB\xC0" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 16,
          "\x3E\x40\xAB\x60\x93\x69\x52\x81\xB3\xAC\x1A\x93\x04\x22\x4D\x98" },
        { KV5M_DATA, 33,
          "\x0F\x9A\xD1\x21\xD9\x9D\x4A\x09\x44\x8E\x4F\x1F\x71\x8C\x4F\x5C"
          "\xBE\x60\x96\x26\x2C\x66\xF2\x9D\xF2\x32\xA8\x7C\x9F\x98\x75\x5D"
          "\x55" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 16,
          "\x4B\xA2\xFB\xF0\x37\x9F\xAE\xD8\x7A\x25\x4D\x3B\x35\x3D\x5A\x7E" },
        { KV5M_DATA, 37,
          "\x61\x2C\x57\x56\x8B\x17\xA7\x03\x52\xBA\xE8\xCF\x26\xFB\x94\x59"
          "\xA6\xF3\x35\x3C\xD3\x5F\xD4\x39\xDB\x31\x07\xCB\xEC\x76\x5D\x32"
          "\x6D\xFC\x04\xC1\xDD" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 16,
          "\x68\xF2\x63\xDB\x3F\xCE\x15\xD0\x31\xC9\xEA\xB0\x2D\x67\x10\x7A" },
        { KV5M_DATA, 54,
          "\x95\xF9\x04\x7C\x3A\xD7\x58\x91\xC2\xE9\xB0\x4B\x16\x56\x6D\xC8"
          "\xB6\xEB\x9C\xE4\x23\x1A\xFB\x25\x42\xEF\x87\xA7\xB5\xA0\xF2\x60"
          "\xA9\x9F\x04\x60\x50\x8D\xE0\xCE\xCC\x63\x2D\x07\xC3\x54\x12\x4E"
          "\x46\xC5\xD2\x23\x4E\xB8" }
    },

    {
        ENCTYPE_ARCFOUR_HMAC_EXP,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 16,
          "\xF7\xD3\xA1\x55\xAF\x5E\x23\x8A\x0B\x7A\x87\x1A\x96\xBA\x2A\xB2" },
        { KV5M_DATA, 24,
          "\x28\x27\xF0\xE9\x0F\x62\xE7\x46\x0C\x4E\x2F\xB3\x9F\x96\x57\xBA"
          "\x8B\xFA\xA9\x91\xD7\xFD\xAD\xFF" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC_EXP,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 16,
          "\xDE\xEA\xA0\x60\x7D\xB7\x99\xE2\xFD\xD6\xDB\x29\x86\xBB\x8D\x65" },
        { KV5M_DATA, 25,
          "\x3D\xDA\x39\x2E\x2E\x27\x5A\x4D\x75\x18\x3F\xA6\x32\x8A\x0A\x4E"
          "\x6B\x75\x2D\xF6\xCD\x2A\x25\xFA\x4E" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC_EXP,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 16,
          "\x33\xAD\x7F\xC2\x67\x86\x15\x56\x9B\x2B\x09\x83\x6E\x0A\x3A\xB6" },
        { KV5M_DATA, 33,
          "\x09\xD1\x36\xAC\x48\x5D\x92\x64\x4E\xC6\x70\x1D\x6A\x0D\x03\xE8"
          "\x98\x2D\x7A\x3C\xA7\xEF\xD0\xF8\xF4\xF8\x36\x60\xEF\x42\x77\xBB"
          "\x81" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC_EXP,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 16,
          "\x39\xF2\x5C\xD4\xF0\xD4\x1B\x2B\x2D\x9D\x30\x0F\xCB\x29\x81\xCB" },
        { KV5M_DATA, 37,
          "\x91\x23\x88\xD7\xC0\x76\x12\x81\x9E\x3B\x64\x0F\xF5\xCE\xCD\xAF"
          "\x72\xE5\xA5\x9D\xF1\x0F\x10\x91\xA6\xBE\xC3\x9C\xAA\xD7\x48\xAF"
          "\x9B\xD2\xD8\xD5\x46" }
    },
    {
        ENCTYPE_ARCFOUR_HMAC_EXP,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 16,
          "\x9F\x72\x55\x42\xD9\xF7\x2A\xA1\xF3\x86\xCB\xE7\x89\x69\x84\xFC" },
        { KV5M_DATA, 54,
          "\x78\xB3\x5A\x08\xB0\x8B\xE2\x65\xAE\xB4\x14\x5F\x07\x65\x13\xB6"
          "\xB5\x6E\xFE\xD3\xF7\x52\x65\x74\xAF\x74\xF7\xD2\xF9\xBA\xE9\x6E"
          "\xAB\xB7\x6F\x2D\x87\x38\x6D\x2E\x93\xE3\xA7\x7B\x99\x91\x9F\x1D"
          "\x97\x64\x90\xE2\xBD\x45" }
    },

    {
        ENCTYPE_AES128_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 16,
          "\x5A\x5C\x0F\x0B\xA5\x4F\x38\x28\xB2\x19\x5E\x66\xCA\x24\xA2\x89" },
        { KV5M_DATA, 28,
          "\x49\xFF\x8E\x11\xC1\x73\xD9\x58\x3A\x32\x54\xFB\xE7\xB1\xF1\xDF"
          "\x36\xC5\x38\xE8\x41\x67\x84\xA1\x67\x2E\x66\x76" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 16,
          "\x98\x45\x0E\x3F\x3B\xAA\x13\xF5\xC9\x9B\xEB\x93\x69\x81\xB0\x6F" },
        { KV5M_DATA, 29,
          "\xF8\x67\x42\xF5\x37\xB3\x5D\xC2\x17\x4A\x4D\xBA\xA9\x20\xFA\xF9"
          "\x04\x20\x90\xB0\x65\xE1\xEB\xB1\xCA\xD9\xA6\x53\x94" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 16,
          "\x90\x62\x43\x0C\x8C\xDA\x33\x88\x92\x2E\x6D\x6A\x50\x9F\x5B\x7A" },
        { KV5M_DATA, 37,
          "\x68\xFB\x96\x79\x60\x1F\x45\xC7\x88\x57\xB2\xBF\x82\x0F\xD6\xE5"
          "\x3E\xCA\x8D\x42\xFD\x4B\x1D\x70\x24\xA0\x92\x05\xAB\xB7\xCD\x2E"
          "\xC2\x6C\x35\x5D\x2F" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 16,
          "\x03\x3E\xE6\x50\x2C\x54\xFD\x23\xE2\x77\x91\xE9\x87\x98\x38\x27" },
        { KV5M_DATA, 41,
          "\xEC\x36\x6D\x03\x27\xA9\x33\xBF\x49\x33\x0E\x65\x0E\x49\xBC\x6B"
          "\x97\x46\x37\xFE\x80\xBF\x53\x2F\xE5\x17\x95\xB4\x80\x97\x18\xE6"
          "\x19\x47\x24\xDB\x94\x8D\x1F\xD6\x37" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 16,
          "\xDC\xEE\xB7\x0B\x3D\xE7\x65\x62\xE6\x89\x22\x6C\x76\x42\x91\x48" },
        { KV5M_DATA, 58,
          "\xC9\x60\x81\x03\x2D\x5D\x8E\xEB\x7E\x32\xB4\x08\x9F\x78\x9D\x0F"
          "\xAA\x48\x1D\xEA\x74\xC0\xF9\x7C\xBF\x31\x46\xDD\xFC\xF8\xE8\x00"
          "\x15\x6E\xCB\x53\x2F\xC2\x03\xE3\x0F\xF6\x00\xB6\x3B\x35\x09\x39"
          "\xFE\xCE\x51\x0F\x02\xD7\xFF\x1E\x7B\xAC" }
    },

    {
        ENCTYPE_AES256_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 32,
          "\x17\xF2\x75\xF2\x95\x4F\x2E\xD1\xF9\x0C\x37\x7B\xA7\xF4\xD6\xA3"
          "\x69\xAA\x01\x36\xE0\xBF\x0C\x92\x7A\xD6\x13\x3C\x69\x37\x59\xA9" },
        { KV5M_DATA, 28,
          "\xE5\x09\x4C\x55\xEE\x7B\x38\x26\x2E\x2B\x04\x42\x80\xB0\x69\x37"
          "\x9A\x95\xBF\x95\xBD\x83\x76\xFB\x32\x81\xB4\x35" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 32,
          "\xB9\x47\x7E\x1F\xF0\x32\x9C\x00\x50\xE2\x0C\xE6\xC7\x2D\x2D\xFF"
          "\x27\xE8\xFE\x54\x1A\xB0\x95\x44\x29\xA9\xCB\x5B\x4F\x7B\x1E\x2A" },
        { KV5M_DATA, 29,
          "\x40\x61\x50\xB9\x7A\xEB\x76\xD4\x3B\x36\xB6\x2C\xC1\xEC\xDF\xBE"
          "\x6F\x40\xE9\x57\x55\xE0\xBE\xB5\xC2\x78\x25\xF3\xA4" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 32,
          "\xB1\xAE\x4C\xD8\x46\x2A\xFF\x16\x77\x05\x3C\xC9\x27\x9A\xAC\x30"
          "\xB7\x96\xFB\x81\xCE\x21\x47\x4D\xD3\xDD\xBC\xFE\xA4\xEC\x76\xD7" },
        { KV5M_DATA, 37,
          "\x09\x95\x7A\xA2\x5F\xCA\xF8\x8F\x7B\x39\xE4\x40\x6E\x63\x30\x12"
          "\xD5\xFE\xA2\x18\x53\xF6\x47\x8D\xA7\x06\x5C\xAE\xF4\x1F\xD4\x54"
          "\xA4\x08\x24\xEE\xC5" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 32,
          "\xE5\xA7\x2B\xE9\xB7\x92\x6C\x12\x25\xBA\xFE\xF9\xC1\x87\x2E\x7B"
          "\xA4\xCD\xB2\xB1\x78\x93\xD8\x4A\xBD\x90\xAC\xDD\x87\x64\xD9\x66" },
        { KV5M_DATA, 41,
          "\xD8\xF1\xAA\xFE\xEC\x84\x58\x7C\xC3\xE7\x00\xA7\x74\xE5\x66\x51"
          "\xA6\xD6\x93\xE1\x74\xEC\x44\x73\xB5\xE6\xD9\x6F\x80\x29\x7A\x65"
          "\x3F\xB8\x18\xAD\x89\x3E\x71\x9F\x96" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA1_96,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 32,
          "\xF1\xC7\x95\xE9\x24\x8A\x09\x33\x8D\x82\xC3\xF8\xD5\xB5\x67\x04"
          "\x0B\x01\x10\x73\x68\x45\x04\x13\x47\x23\x5B\x14\x04\x23\x13\x98" },
        { KV5M_DATA, 58,
          "\xD1\x13\x7A\x4D\x63\x4C\xFE\xCE\x92\x4D\xBC\x3B\xF6\x79\x06\x48"
          "\xBD\x5C\xFF\x7D\xE0\xE7\xB9\x94\x60\x21\x1D\x0D\xAE\xF3\xD7\x9A"
          "\x29\x5C\x68\x88\x58\xF3\xB3\x4B\x9C\xBD\x6E\xEB\xAE\x81\xDA\xF6"
          "\xB7\x34\xD4\xD4\x98\xB6\x71\x4F\x1C\x1D" }
    },

    {
        ENCTYPE_CAMELLIA128_CTS_CMAC,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 16,
          "\x1D\xC4\x6A\x8D\x76\x3F\x4F\x93\x74\x2B\xCB\xA3\x38\x75\x76\xC3" },
        { KV5M_DATA, 32,
          "\xC4\x66\xF1\x87\x10\x69\x92\x1E\xDB\x7C\x6F\xDE\x24\x4A\x52\xDB"
          "\x0B\xA1\x0E\xDC\x19\x7B\xDB\x80\x06\x65\x8C\xA3\xCC\xCE\x6E\xB8" }
    },
    {
        ENCTYPE_CAMELLIA128_CTS_CMAC,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 16,
          "\x50\x27\xBC\x23\x1D\x0F\x3A\x9D\x23\x33\x3F\x1C\xA6\xFD\xBE\x7C" },
        { KV5M_DATA, 33,
          "\x84\x2D\x21\xFD\x95\x03\x11\xC0\xDD\x46\x4A\x3F\x4B\xE8\xD6\xDA"
          "\x88\xA5\x6D\x55\x9C\x9B\x47\xD3\xF9\xA8\x50\x67\xAF\x66\x15\x59"
          "\xB8" }
    },
    {
        ENCTYPE_CAMELLIA128_CTS_CMAC,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 16,
          "\xA1\xBB\x61\xE8\x05\xF9\xBA\x6D\xDE\x8F\xDB\xDD\xC0\x5C\xDE\xA0" },
        { KV5M_DATA, 41,
          "\x61\x9F\xF0\x72\xE3\x62\x86\xFF\x0A\x28\xDE\xB3\xA3\x52\xEC\x0D"
          "\x0E\xDF\x5C\x51\x60\xD6\x63\xC9\x01\x75\x8C\xCF\x9D\x1E\xD3\x3D"
          "\x71\xDB\x8F\x23\xAA\xBF\x83\x48\xA0" }
    },
    {
        ENCTYPE_CAMELLIA128_CTS_CMAC,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 16,
          "\x2C\xA2\x7A\x5F\xAF\x55\x32\x24\x45\x06\x43\x4E\x1C\xEF\x66\x76" },
        { KV5M_DATA, 45,
          "\xB8\xEC\xA3\x16\x7A\xE6\x31\x55\x12\xE5\x9F\x98\xA7\xC5\x00\x20"
          "\x5E\x5F\x63\xFF\x3B\xB3\x89\xAF\x1C\x41\xA2\x1D\x64\x0D\x86\x15"
          "\xC9\xED\x3F\xBE\xB0\x5A\xB6\xAC\xB6\x76\x89\xB5\xEA" }
    },
    {
        ENCTYPE_CAMELLIA128_CTS_CMAC,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 16,
          "\x78\x24\xF8\xC1\x6F\x83\xFF\x35\x4C\x6B\xF7\x51\x5B\x97\x3F\x43" },
        { KV5M_DATA, 62,
          "\xA2\x6A\x39\x05\xA4\xFF\xD5\x81\x6B\x7B\x1E\x27\x38\x0D\x08\x09"
          "\x0C\x8E\xC1\xF3\x04\x49\x6E\x1A\xBD\xCD\x2B\xDC\xD1\xDF\xFC\x66"
          "\x09\x89\xE1\x17\xA7\x13\xDD\xBB\x57\xA4\x14\x6C\x15\x87\xCB\xA4"
          "\x35\x66\x65\x59\x1D\x22\x40\x28\x2F\x58\x42\xB1\x05\xA5" }
    },

    {
        ENCTYPE_CAMELLIA256_CTS_CMAC,
        { KV5M_DATA, 0, "", }, 0,
        { KV5M_DATA, 32,
          "\xB6\x1C\x86\xCC\x4E\x5D\x27\x57\x54\x5A\xD4\x23\x39\x9F\xB7\x03"
          "\x1E\xCA\xB9\x13\xCB\xB9\x00\xBD\x7A\x3C\x6D\xD8\xBF\x92\x01\x5B" },
        { KV5M_DATA, 32,
          "\x03\x88\x6D\x03\x31\x0B\x47\xA6\xD8\xF0\x6D\x7B\x94\xD1\xDD\x83"
          "\x7E\xCC\xE3\x15\xEF\x65\x2A\xFF\x62\x08\x59\xD9\x4A\x25\x92\x66" }
    },
    {
        ENCTYPE_CAMELLIA256_CTS_CMAC,
        { KV5M_DATA, 1, "1", }, 1,
        { KV5M_DATA, 32,
          "\x1B\x97\xFE\x0A\x19\x0E\x20\x21\xEB\x30\x75\x3E\x1B\x6E\x1E\x77"
          "\xB0\x75\x4B\x1D\x68\x46\x10\x35\x58\x64\x10\x49\x63\x46\x38\x33" },
        { KV5M_DATA, 33,
          "\x2C\x9C\x15\x70\x13\x3C\x99\xBF\x6A\x34\xBC\x1B\x02\x12\x00\x2F"
          "\xD1\x94\x33\x87\x49\xDB\x41\x35\x49\x7A\x34\x7C\xFC\xD9\xD1\x8A"
          "\x12" }
    },
    {
        ENCTYPE_CAMELLIA256_CTS_CMAC,
        { KV5M_DATA, 9, "9 bytesss", }, 2,
        { KV5M_DATA, 32,
          "\x32\x16\x4C\x5B\x43\x4D\x1D\x15\x38\xE4\xCF\xD9\xBE\x80\x40\xFE"
          "\x8C\x4A\xC7\xAC\xC4\xB9\x3D\x33\x14\xD2\x13\x36\x68\x14\x7A\x05" },
        { KV5M_DATA, 41,
          "\x9C\x6D\xE7\x5F\x81\x2D\xE7\xED\x0D\x28\xB2\x96\x35\x57\xA1\x15"
          "\x64\x09\x98\x27\x5B\x0A\xF5\x15\x27\x09\x91\x3F\xF5\x2A\x2A\x9C"
          "\x8E\x63\xB8\x72\xF9\x2E\x64\xC8\x39" }
    },
    {
        ENCTYPE_CAMELLIA256_CTS_CMAC,
        { KV5M_DATA, 13, "13 bytes byte", }, 3,
        { KV5M_DATA, 32,
          "\xB0\x38\xB1\x32\xCD\x8E\x06\x61\x22\x67\xFA\xB7\x17\x00\x66\xD8"
          "\x8A\xEC\xCB\xA0\xB7\x44\xBF\xC6\x0D\xC8\x9B\xCA\x18\x2D\x07\x15" },
        { KV5M_DATA, 45,
          "\xEE\xEC\x85\xA9\x81\x3C\xDC\x53\x67\x72\xAB\x9B\x42\xDE\xFC\x57"
          "\x06\xF7\x26\xE9\x75\xDD\xE0\x5A\x87\xEB\x54\x06\xEA\x32\x4C\xA1"
          "\x85\xC9\x98\x6B\x42\xAA\xBE\x79\x4B\x84\x82\x1B\xEE" }
    },
    {
        ENCTYPE_CAMELLIA256_CTS_CMAC,
        { KV5M_DATA, 30, "30 bytes bytes bytes bytes byt", }, 4,
        { KV5M_DATA, 32,
          "\xCC\xFC\xD3\x49\xBF\x4C\x66\x77\xE8\x6E\x4B\x02\xB8\xEA\xB9\x24"
          "\xA5\x46\xAC\x73\x1C\xF9\xBF\x69\x89\xB9\x96\xE7\xD6\xBF\xBB\xA7" },
        { KV5M_DATA, 62,
          "\x0E\x44\x68\x09\x85\x85\x5F\x2D\x1F\x18\x12\x52\x9C\xA8\x3B\xFD"
          "\x8E\x34\x9D\xE6\xFD\x9A\xDA\x0B\xAA\xA0\x48\xD6\x8E\x26\x5F\xEB"
          "\xF3\x4A\xD1\x25\x5A\x34\x49\x99\xAD\x37\x14\x68\x87\xA6\xC6\x84"
          "\x57\x31\xAC\x7F\x46\x37\x6A\x05\x04\xCD\x06\x57\x14\x74" }
    },

    {
        ENCTYPE_AES128_CTS_HMAC_SHA256_128,
        { KV5M_DATA, 0, "", }, 2,
        { KV5M_DATA, 16,
          "\x37\x05\xD9\x60\x80\xC1\x77\x28\xA0\xE8\x00\xEA\xB6\xE0\xD2\x3C" },
        { KV5M_DATA, 32,
          "\xEF\x85\xFB\x89\x0B\xB8\x47\x2F\x4D\xAB\x20\x39\x4D\xCA\x78\x1D"
          "\xAD\x87\x7E\xDA\x39\xD5\x0C\x87\x0C\x0D\x5A\x0A\x8E\x48\xC7\x18" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA256_128,
        { KV5M_DATA, 6, "\x00\x01\x02\x03\x04\x05", }, 2,
        { KV5M_DATA, 16,
          "\x37\x05\xD9\x60\x80\xC1\x77\x28\xA0\xE8\x00\xEA\xB6\xE0\xD2\x3C" },
        { KV5M_DATA, 38,
          "\x84\xD7\xF3\x07\x54\xED\x98\x7B\xAB\x0B\xF3\x50\x6B\xEB\x09\xCF"
          "\xB5\x54\x02\xCE\xF7\xE6\x87\x7C\xE9\x9E\x24\x7E\x52\xD1\x6E\xD4"
          "\x42\x1D\xFD\xF8\x97\x6C" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA256_128,
        { KV5M_DATA, 16,
          "\x00\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0A\x0B\x0C\x0D\x0E\x0F" },
        2,
        { KV5M_DATA, 16,
          "\x37\x05\xD9\x60\x80\xC1\x77\x28\xA0\xE8\x00\xEA\xB6\xE0\xD2\x3C" },
        { KV5M_DATA, 48,
          "\x35\x17\xD6\x40\xF5\x0D\xDC\x8A\xD3\x62\x87\x22\xB3\x56\x9D\x2A"
          "\xE0\x74\x93\xFA\x82\x63\x25\x40\x80\xEA\x65\xC1\x00\x8E\x8F\xC2"
          "\x95\xFB\x48\x52\xE7\xD8\x3E\x1E\x7C\x48\xC3\x7E\xEB\xE6\xB0\xD3" }
    },
    {
        ENCTYPE_AES128_CTS_HMAC_SHA256_128,
        { KV5M_DATA, 21,
          "\x00\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0A\x0B\x0C\x0D\x0E\x0F"
          "\x10\x11\x12\x13\x14" },
        2,
        { KV5M_DATA, 16,
          "\x37\x05\xD9\x60\x80\xC1\x77\x28\xA0\xE8\x00\xEA\xB6\xE0\xD2\x3C" },
        { KV5M_DATA, 53,
          "\x72\x0F\x73\xB1\x8D\x98\x59\xCD\x6C\xCB\x43\x46\x11\x5C\xD3\x36"
          "\xC7\x0F\x58\xED\xC0\xC4\x43\x7C\x55\x73\x54\x4C\x31\xC8\x13\xBC"
          "\xE1\xE6\xD0\x72\xC1\x86\xB3\x9A\x41\x3C\x2F\x92\xCA\x9B\x83\x34"
          "\xA2\x87\xFF\xCB\xFC" }
    },

    {
        ENCTYPE_AES256_CTS_HMAC_SHA384_192,
        { KV5M_DATA, 0, "", }, 2,
        { KV5M_DATA, 32,
          "\x6D\x40\x4D\x37\xFA\xF7\x9F\x9D\xF0\xD3\x35\x68\xD3\x20\x66\x98"
          "\x00\xEB\x48\x36\x47\x2E\xA8\xA0\x26\xD1\x6B\x71\x82\x46\x0C\x52" },
        { KV5M_DATA, 40,
          "\x41\xF5\x3F\xA5\xBF\xE7\x02\x6D\x91\xFA\xF9\xBE\x95\x91\x95\xA0"
          "\x58\x70\x72\x73\xA9\x6A\x40\xF0\xA0\x19\x60\x62\x1A\xC6\x12\x74"
          "\x8B\x9B\xBF\xBE\x7E\xB4\xCE\x3C" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA384_192,
        { KV5M_DATA, 6, "\x00\x01\x02\x03\x04\x05", }, 2,
        { KV5M_DATA, 32,
          "\x6D\x40\x4D\x37\xFA\xF7\x9F\x9D\xF0\xD3\x35\x68\xD3\x20\x66\x98"
          "\x00\xEB\x48\x36\x47\x2E\xA8\xA0\x26\xD1\x6B\x71\x82\x46\x0C\x52" },
        { KV5M_DATA, 46,
          "\x4E\xD7\xB3\x7C\x2B\xCA\xC8\xF7\x4F\x23\xC1\xCF\x07\xE6\x2B\xC7"
          "\xB7\x5F\xB3\xF6\x37\xB9\xF5\x59\xC7\xF6\x64\xF6\x9E\xAB\x7B\x60"
          "\x92\x23\x75\x26\xEA\x0D\x1F\x61\xCB\x20\xD6\x9D\x10\xF2" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA384_192,
        { KV5M_DATA, 16,
          "\x00\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0A\x0B\x0C\x0D\x0E\x0F" },
        2,
        { KV5M_DATA, 32,
          "\x6D\x40\x4D\x37\xFA\xF7\x9F\x9D\xF0\xD3\x35\x68\xD3\x20\x66\x98"
          "\x00\xEB\x48\x36\x47\x2E\xA8\xA0\x26\xD1\x6B\x71\x82\x46\x0C\x52" },
        { KV5M_DATA, 56,
          "\xBC\x47\xFF\xEC\x79\x98\xEB\x91\xE8\x11\x5C\xF8\xD1\x9D\xAC\x4B"
          "\xBB\xE2\xE1\x63\xE8\x7D\xD3\x7F\x49\xBE\xCA\x92\x02\x77\x64\xF6"
          "\x8C\xF5\x1F\x14\xD7\x98\xC2\x27\x3F\x35\xDF\x57\x4D\x1F\x93\x2E"
          "\x40\xC4\xFF\x25\x5B\x36\xA2\x66" }
    },
    {
        ENCTYPE_AES256_CTS_HMAC_SHA384_192,
        { KV5M_DATA, 21,
          "\x00\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0A\x0B\x0C\x0D\x0E\x0F"
          "\x10\x11\x12\x13\x14" },
        2,
        { KV5M_DATA, 32,
          "\x6D\x40\x4D\x37\xFA\xF7\x9F\x9D\xF0\xD3\x35\x68\xD3\x20\x66\x98"
          "\x00\xEB\x48\x36\x47\x2E\xA8\xA0\x26\xD1\x6B\x71\x82\x46\x0C\x52" },
        { KV5M_DATA, 61,
          "\x40\x01\x3E\x2D\xF5\x8E\x87\x51\x95\x7D\x28\x78\xBC\xD2\xD6\xFE"
          "\x10\x1C\xCF\xD5\x56\xCB\x1E\xAE\x79\xDB\x3C\x3E\xE8\x64\x29\xF2"
          "\xB2\xA6\x02\xAC\x86\xFE\xF6\xEC\xB6\x47\xD6\x29\x5F\xAE\x07\x7A"
          "\x1F\xEB\x51\x75\x08\xD2\xC1\x6B\x41\x92\xE0\x1F\x62" }
    },
};

static void
printhex(const char *head, void *data, size_t len)
{
    size_t i;

    printf("%s", head);
    for (i = 0; i < len; i++) {
        printf("%02X", ((unsigned char*)data)[i]);
        if (i % 16 == 15 && i + 1 < len)
            printf("\n%*s", (int)strlen(head), "");
        else if (i + 1 < len)
            printf(" ");
    }
    printf("\n");
}

static krb5_enctype
enctypes[] = {
    ENCTYPE_DES3_CBC_SHA1,
    ENCTYPE_ARCFOUR_HMAC,
    ENCTYPE_ARCFOUR_HMAC_EXP,
    ENCTYPE_AES128_CTS_HMAC_SHA1_96,
    ENCTYPE_AES256_CTS_HMAC_SHA1_96,
    ENCTYPE_CAMELLIA128_CTS_CMAC,
    ENCTYPE_CAMELLIA256_CTS_CMAC,
    ENCTYPE_AES128_CTS_HMAC_SHA256_128,
    ENCTYPE_AES256_CTS_HMAC_SHA384_192
};

static char *plaintexts[] = {
    "",
    "1",
    "9 bytesss",
    "13 bytes byte",
    "30 bytes bytes bytes bytes byt"
};

static int
generate(krb5_context context)
{
    krb5_error_code ret;
    size_t i, j;
    krb5_keyblock kb;
    krb5_data plain, seed = string2data("seed");
    krb5_enc_data enc;
    size_t enclen;
    char buf[64];

    ret = krb5_c_random_seed(context, &seed);
    assert(!ret);
    for (i = 0; i < sizeof(enctypes) / sizeof(*enctypes); i++) {
        for (j = 0; j < sizeof(plaintexts) / sizeof(*plaintexts); j++) {
            ret = krb5_c_make_random_key(context, enctypes[i], &kb);
            assert(!ret);
            plain = string2data(plaintexts[j]);
            ret = krb5_c_encrypt_length(context, enctypes[i], plain.length,
                                        &enclen);
            assert(!ret);
            ret = alloc_data(&enc.ciphertext, enclen);
            assert(!ret);
            ret = krb5_c_encrypt(context, &kb, j, NULL, &plain, &enc);
            assert(!ret);
            krb5_enctype_to_name(enctypes[i], FALSE, buf, sizeof(buf));
            printf("\nEnctype: %s\n", buf);
            printf("Plaintext: %s\n", plaintexts[j]);
            printhex("Key: ", kb.contents, kb.length);
            printhex("Ciphertext: ", enc.ciphertext.data,
                     enc.ciphertext.length);
            free(enc.ciphertext.data);
        }
    }
    return 0;
}

int
main(int argc, char **argv)
{
    krb5_error_code ret;
    krb5_context context = NULL;
    krb5_data plain;
    size_t i;
    struct test *test;
    krb5_keyblock kb;
    krb5_enc_data enc;

    if (argc >= 2 && strcmp(argv[1], "-g") == 0)
        return generate(context);

    for (i = 0; i < sizeof(test_cases) / sizeof(*test_cases); i++) {
        test = &test_cases[i];
        kb.magic = KV5M_KEYBLOCK;
        kb.enctype = test->enctype;
        kb.length = test->keybits.length;
        kb.contents = (unsigned char *)test->keybits.data;
        ret = alloc_data(&plain, test->ciphertext.length);
        assert(!ret);
        enc.magic = KV5M_ENC_DATA;
        enc.enctype = test->enctype;
        enc.kvno = 0;
        enc.ciphertext = test->ciphertext;
        if (krb5_c_decrypt(context, &kb, test->usage, NULL, &enc,
                           &plain) != 0) {
            printf("decrypt test %d failed to decrypt\n", (int)i);
            return 1;
        }
        assert(plain.length >= test->plaintext.length);
        if (memcmp(plain.data, test->plaintext.data,
                   test->plaintext.length) != 0) {
            printf("decrypt test %d produced wrong result\n", (int)i);
            return 1;
        }
        free(plain.data);
    }
    return 0;
}
