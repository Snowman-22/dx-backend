package com.snowman.team2.global.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** S3 객체 키 앞에 붙는 프리픽스 (비어 있으면 버킷 루트에 바로 저장) */
@Getter
@RequiredArgsConstructor
public enum S3UploadPath {
    /** 상품 이미지: 프리픽스 없음 → 키 예: {@code uuid.png} */
    PRODUCT_IMAGE(""),
    BLUEPRINT_IMAGE("blueprint"),
    /** 프론트가 보낸 2D 레이아웃 이미지 업로드 */
    LAYOUT_2D("layout2d"),
    /** 프론트가 보낸 3D 레이아웃 이미지 업로드 */
    LAYOUT_3D("layout3d");

    private final String prefix;
}
