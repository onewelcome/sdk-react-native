extension Result where Success == Void {
    public static var success: Result { .success(()) }
}
