"""يعيد بناء manifest.json بعد إضافة أو تعديل أو حذف ملفات التمارين.

الاستعمال:  python3 tools/build_manifest.py
"""
import hashlib, json, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "app/src/main/assets/exercises"
files = {
    p.relative_to(root).as_posix(): hashlib.sha256(p.read_bytes()).hexdigest()
    for p in sorted(root.rglob("*.json"))
    if p.name != "manifest.json"
}
(root / "manifest.json").write_text(json.dumps({"files": files}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
print(f"manifest.json: {len(files)} files")
