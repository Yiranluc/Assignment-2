public class Swipe {
  private String swiper;
  private String swipee;
  private String comment;

  public Swipe(String swiper, String swipee, String comment) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  public boolean isSwipeRequestValid() {
    return this.swipee != null && this.swiper != null && this.comment != null;
  }
}
