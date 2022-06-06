/*
 * Copyright 2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "KString.h"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

TEST(KStringTest, mallocString_ascii) {
    auto result = mallocString("ascii")->array();
    EXPECT_THAT(result->count_, 5);
}
