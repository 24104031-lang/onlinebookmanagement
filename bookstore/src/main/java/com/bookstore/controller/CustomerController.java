package com.bookstore.controller;

import com.bookstore.model.*;
import com.bookstore.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class CustomerController {

    @Autowired private BookService bookService;
    @Autowired private OrderService orderService;
    @Autowired private ReviewService reviewService;
    @Autowired private WishlistService wishlistService;
    @Autowired private UserService userService;

    // ─── BROWSE BOOKS ───────────────────────────────────────────────────────────

    @GetMapping("/customer/books")
    public String browseBooks(Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String category,
                              Authentication auth) {
        List<Book> books;
        if (search != null && !search.isBlank()) {
            books = bookService.search(search);
            model.addAttribute("search", search);
        } else if (category != null && !category.isBlank()) {
            books = bookService.getByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            books = bookService.getAllBooks();
        }
        model.addAttribute("books", books);
        model.addAttribute("categories", bookService.getAllCategories());

        // Wishlist set for icons
        String userId = auth.getName();
        List<Book> wishlist = wishlistService.getWishlist(userId);
        Set<String> wishlistIds = new HashSet<>();
        wishlist.forEach(b -> wishlistIds.add(b.getBookId()));
        model.addAttribute("wishlistIds", wishlistIds);
        return "customer/books";
    }

    @GetMapping("/customer/books/{bookId}")
    public String bookDetail(@PathVariable String bookId, Model model, Authentication auth) {
        bookService.findByBookId(bookId).ifPresent(book -> {
            model.addAttribute("book", book);
            model.addAttribute("reviews", reviewService.getReviewsForBook(bookId));
            model.addAttribute("avgRating", reviewService.getAvgRating(bookId));
            model.addAttribute("inWishlist", wishlistService.isInWishlist(auth.getName(), bookId));
        });
        return "customer/book-detail";
    }

    // ─── CART (Session-based) ────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Map<String, CartItem> getCart(HttpSession session) {
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam String bookId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes ra) {
        bookService.findByBookId(bookId).ifPresentOrElse(book -> {
            if (!book.isEbook() && book.getStock() < quantity) {
                ra.addFlashAttribute("error", "Only " + book.getStock() + " copies available.");
                return;
            }
            Map<String, CartItem> cart = getCart(session);
            if (cart.containsKey(bookId)) {
                CartItem existing = cart.get(bookId);
                existing.setQuantity(existing.getQuantity() + quantity);
            } else {
                cart.put(bookId, new CartItem(book, quantity));
            }
            ra.addFlashAttribute("success", "\"" + book.getTitle() + "\" added to cart!");
        }, () -> ra.addFlashAttribute("error", "Book not found."));
        return "redirect:/customer/books";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Map<String, CartItem> cart = getCart(session);
        double total = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("cartTotal", total);
        return "customer/cart";
    }

    @PostMapping("/cart/remove/{bookId}")
    public String removeFromCart(@PathVariable String bookId, HttpSession session) {
        getCart(session).remove(bookId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{bookId}")
    public String updateCartQty(@PathVariable String bookId,
                                @RequestParam int quantity,
                                HttpSession session,
                                RedirectAttributes ra) {
        Map<String, CartItem> cart = getCart(session);
        if (cart.containsKey(bookId)) {
            if (quantity <= 0) {
                cart.remove(bookId);
            } else {
                cart.get(bookId).setQuantity(quantity);
            }
        }
        return "redirect:/cart";
    }

    // ─── CHECKOUT ────────────────────────────────────────────────────────────────

    @GetMapping("/cart/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Map<String, CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";
        double total = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("cartTotal", total);
        return "customer/checkout";
    }

    @PostMapping("/cart/checkout")
    public String placeOrder(@RequestParam String paymentMethod,
                             HttpSession session,
                             Authentication auth,
                             RedirectAttributes ra) {
        Map<String, CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        boolean success = orderService.placeOrder(auth.getName(), new ArrayList<>(cart.values()), paymentMethod);
        if (success) {
            session.removeAttribute("cart");
            ra.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/orders";
        }
        ra.addFlashAttribute("error", "Order failed. Try again.");
        return "redirect:/cart";
    }

    // ─── ORDERS ──────────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    public String myOrders(Authentication auth, Model model) {
        model.addAttribute("orders", orderService.getOrdersByCustomer(auth.getName()));
        return "customer/orders";
    }

    // ─── WISHLIST ────────────────────────────────────────────────────────────────

    @PostMapping("/wishlist/add/{bookId}")
    public String addToWishlist(@PathVariable String bookId,
                                Authentication auth,
                                RedirectAttributes ra) {
        wishlistService.add(auth.getName(), bookId);
        ra.addFlashAttribute("success", "Added to wishlist.");
        return "redirect:/customer/books/" + bookId;
    }

    @PostMapping("/wishlist/remove/{bookId}")
    public String removeFromWishlist(@PathVariable String bookId,
                                     Authentication auth,
                                     RedirectAttributes ra) {
        wishlistService.remove(auth.getName(), bookId);
        ra.addFlashAttribute("success", "Removed from wishlist.");
        return "redirect:/wishlist";
    }

    @GetMapping("/wishlist")
    public String myWishlist(Authentication auth, Model model) {
        model.addAttribute("wishlistBooks", wishlistService.getWishlist(auth.getName()));
        return "customer/wishlist";
    }

    // ─── REVIEWS ─────────────────────────────────────────────────────────────────

    @PostMapping("/review/add")
    public String addReview(@RequestParam String bookId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Authentication auth,
                            RedirectAttributes ra) {
        bookService.findByBookId(bookId).ifPresent(book -> {
            String customerName = userService.findByUserId(auth.getName())
                .map(User::getName).orElse(auth.getName());
            reviewService.addReview(bookId, book.getTitle(), auth.getName(), customerName, rating, comment);
        });
        ra.addFlashAttribute("success", "Review submitted!");
        return "redirect:/customer/books/" + bookId;
    }
}
